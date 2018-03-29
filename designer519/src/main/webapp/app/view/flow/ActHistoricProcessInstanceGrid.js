/* global Ext */

Ext.define('MyApp.view.flow.ActHistoricProcessInstanceGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.acthistoricprocessinstancegrid',
    title: '流程实例管理',
    config: {idName: 'id'},
    rowLines: true,
    columnLines: true,
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'businessKey',
                   flex: 2,
                    text: '业务编号'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    align: 'center',
                    flex: 2,
                    text: '流程实例ID'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'processDefinitionId',
                    align: 'center',
                     flex: 2,
                    text: '流程定义ID'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'startUserId',
                    align: 'center',
                    text: '发起人ID'
                },
                {
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    dataIndex: 'startTime',
                    flex: 1,
                    align: 'center',
                    text: '开始时间',
                    renderer: function (value) {
                        if (value) {
                        return Ext.util.Format.date(new Date(parseInt(value)), 'Y-m-d H:i:s');//Y/m/d H:i
                        }
                    }
                },
                {
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    dataIndex: 'endTime',
                    flex: 1,
                    align: 'center',
                    text: '结束时间',
                    renderer: function (value) {
                        if (value) {
                          return Ext.util.Format.date(new Date(parseInt(value)), 'Y-m-d H:i:s');//Y/m/d H:i
                        }
                    }
                },
                {
                    xtype: 'actioncolumn',
                    text: '查看',
                    align: 'center',
                    flex: 1,
                    items: [
                        {
                            iconCls: 'icon-picture',
                            tooltip: '查看流程跟踪图',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActhistoricprocessinstancegridActioncolumnPicClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        }
                    ]
                }
                

            ],
            viewConfig: {
                enableTextSelection: true,
                stripeRows: true,
                loadMask: {msg: '正在加载数据...'}
            },
            selModel: {
                selType: 'checkboxmodel',
                mode: 'SIMPLE',
                checkOnly: true
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'button',
                            iconCls: 'icon-delete',
                            action: 'delete',
                            width: 80,
                            text: '删除'
                        }
                    ]
                },
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    displayInfo: true,
                    items: ['->'],
                    prependButtons: true
                }

            ]
        });

        me.callParent(arguments);
    }


});












