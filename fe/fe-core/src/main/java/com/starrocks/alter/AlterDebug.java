// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// This file is based on code available under the Apache license here:
//   https://github.com/apache/incubator-doris/blob/master/fe/fe-core/src/main/java/org/apache/doris/alter/Alter.java

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.starrocks.alter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.starrocks.analysis.IntLiteral;
import com.starrocks.analysis.StringLiteral;
import com.starrocks.analysis.TableName;
import com.starrocks.analysis.TableRef;
import com.starrocks.catalog.ColocateTableIndex;
import com.starrocks.catalog.Column;
import com.starrocks.catalog.DataProperty;
import com.starrocks.catalog.Database;
import com.starrocks.catalog.ExpressionRangePartitionInfo;
import com.starrocks.catalog.KeysType;
import com.starrocks.catalog.MaterializedIndex;
import com.starrocks.catalog.MaterializedView;
import com.starrocks.catalog.OlapTable;
import com.starrocks.catalog.OlapTable.OlapTableState;
import com.starrocks.catalog.Partition;
import com.starrocks.catalog.PartitionInfo;
import com.starrocks.catalog.PartitionType;
import com.starrocks.catalog.Table;
import com.starrocks.catalog.Table.TableType;
import com.starrocks.catalog.TableProperty;
import com.starrocks.catalog.View;
import com.starrocks.common.AnalysisException;
import com.starrocks.common.DdlException;
import com.starrocks.common.ErrorCode;
import com.starrocks.common.ErrorReport;
import com.starrocks.common.MetaNotFoundException;
import com.starrocks.common.UserException;
import com.starrocks.common.util.DynamicPartitionUtil;
import com.starrocks.common.util.PropertyAnalyzer;
import com.starrocks.persist.AlterViewInfo;
import com.starrocks.persist.BatchModifyPartitionsInfo;
import com.starrocks.persist.ChangeMaterializedViewRefreshSchemeLog;
import com.starrocks.persist.ModifyPartitionInfo;
import com.starrocks.persist.ModifyTablePropertyOperationLog;
import com.starrocks.persist.RenameMaterializedViewLog;
import com.starrocks.persist.SwapTableOperationLog;
import com.starrocks.qe.ConnectContext;
import com.starrocks.qe.ShowResultSet;
import com.starrocks.scheduler.Constants;
import com.starrocks.scheduler.Task;
import com.starrocks.scheduler.TaskBuilder;
import com.starrocks.scheduler.TaskManager;
import com.starrocks.scheduler.mv.MVManager;
import com.starrocks.server.GlobalStateMgr;
import com.starrocks.sql.analyzer.SetStmtAnalyzer;
import com.starrocks.sql.ast.AddPartitionClause;
import com.starrocks.sql.ast.AlterClause;
import com.starrocks.sql.ast.AlterMaterializedViewStmt;
import com.starrocks.sql.ast.AlterSystemStmt;
import com.starrocks.sql.ast.AlterTableStmt;
import com.starrocks.sql.ast.AlterViewStmt;
import com.starrocks.sql.ast.AsyncRefreshSchemeDesc;
import com.starrocks.sql.ast.ColumnRenameClause;
import com.starrocks.sql.ast.CreateMaterializedViewStmt;
import com.starrocks.sql.ast.DropMaterializedViewStmt;
import com.starrocks.sql.ast.DropPartitionClause;
import com.starrocks.sql.ast.IntervalLiteral;
import com.starrocks.sql.ast.ModifyPartitionClause;
import com.starrocks.sql.ast.ModifyTablePropertiesClause;
import com.starrocks.sql.ast.PartitionRenameClause;
import com.starrocks.sql.ast.RefreshSchemeDesc;
import com.starrocks.sql.ast.ReplacePartitionClause;
import com.starrocks.sql.ast.RollupRenameClause;
import com.starrocks.sql.ast.SetListItem;
import com.starrocks.sql.ast.SetStmt;
import com.starrocks.sql.ast.SwapTableClause;
import com.starrocks.sql.ast.SystemVariable;
import com.starrocks.sql.ast.TableRenameClause;
import com.starrocks.sql.ast.TruncatePartitionClause;
import com.starrocks.sql.ast.TruncateTableStmt;
import com.starrocks.sql.optimizer.Utils;
import com.starrocks.thrift.TTabletMetaType;
import com.starrocks.thrift.TTabletType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.starrocks.catalog.TableProperty.INVALID;

public class AlterDebug {
    private static final Logger LOG = LogManager.getLogger(AlterDebug.class);

    private AlterHandler schemaChangeHandler;
    private AlterHandler materializedViewHandler;
    private SystemHandler clusterHandler;

    public AlterDebug() {
        schemaChangeHandler = new SchemaChangeHandler();
        materializedViewHandler = new MaterializedViewHandler();
        clusterHandler = new SystemHandler();
        System.out.println("DEBUG !!!!!!!!");
    }
}
