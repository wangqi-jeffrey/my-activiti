/* global Ext */

Ext.define('MyApp.view.flow.ActProcessDefinitionGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.actprocessdefinitiongrid',
    title: '流程定义管理',
    config: {idName: 'deploymentId'},
    rowLines: true,
    columnLines: true,
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'id',
                   flex: 2,
                    text: 'ProcessDefinitionId'
                },
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'deploymentId',
                    flex: 2,
                    text: 'DeploymentId'
                },
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'name',
                    text: '名称',
                    flex: 2,
                    editor: {
                        xtype: 'textfield',
                        allowBlank: false
                    },
                    renderer: function (value, metaData, data) {
                        if (value) {
                            return '<span data-qtip="双击可修改名称">' + value + '</span>';
                        } else {
                            return '<span data-qtip="双击可修改名称"></span>';
                        }
                    }
                },
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'keyName',
                    text: 'KEY'
                },
                {
                    xtype: 'numbercolumn',
                    format: '0',
                    dataIndex: 'version',
                    align: 'center',
                    text: '版本'
                },
                {
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    dataIndex: 'deploymentTime',
                    flex: 2,
                    align: 'center',
                    text: '部署时间',
                    renderer: function (value) {
                        if (value === null || value === 0) {
                            return 'null';
                        } else {
                            return Ext.util.Format.date(new Date(parseInt(value)), 'Y-m-d H:i:s');//Y/m/d H:i
                        }
                    }
                },
                {
                    xtype: 'booleancolumn',
                    dataIndex: 'suspended',
                    flex: 1,
                    align: 'center',
                    text: '是否挂起',
                    trueText: '是',
                    falseText: '否'
                },
                {
                    xtype: 'actioncolumn',
                    text: '查看',
                    align: 'center',
                    flex: 1,
                    editor: {xtype: 'label'}, //加上这行是因为同时使用actioncolumn和rowediting时有bug,双击或单击编辑数据会报错
                    items: [{
                            iconCls: 'icon-picture',
                            tooltip: '查看所选模型流程图',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnPicClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-xml',
                            tooltip: '查看所选模型流程XML',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnXmlClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    align: 'center',
                    flex: 2,
                    editor: {xtype: 'label'}, //加上这行是因为同时使用actioncolumn和rowediting时有bug,双击或单击编辑数据会报错
                    items: [
                        {
                            iconCls: 'icon-start',
                            tooltip: '启动流程',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnStartClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-switch',
                            tooltip: '改变流程状态',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnStateClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-model',
                            tooltip: '将流程转换为模型',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnConvertClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-download',
                            tooltip: '下载流程定义文件',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnDownloadClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-delete',
                            tooltip: '删除所选流程',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActprocessdefinitiongridActioncolumnDeleteClick', view, rowIndex, colIndex, item, e, record, row);
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
            plugins: {
                ptype: 'rowediting',
                saveBtnText: '确定',
                cancelBtnText: '取消'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'form',
                            layout: 'fit',
                            border: false,
                            width: 130,
                            items: [
                                {
                                    xtype: 'filefield',
                                    name: 'file',
                                    buttonOnly: true,
                                    buttonConfig: {
                                        width: 130,
                                        text: '部署外部文件'
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'container',
                            defaults: {margin: '0 5 0 0'},
                            layout: {
                                type: 'hbox',
                                align: 'stretchmax'
                            },
                            items: [
                                {
                                    xtype: 'textfield',
                                    name: 'name',
                                    width: 200,
                                    emptyText: '输入关键字查询'
                                },
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-search',
                                    action: 'search',
                                    width: 60,
                                    text: '查询'
                                },
                                {
                                    xtype: 'button',
                                    iconCls: 'icon-refresh',
                                    action: 'refresh',
                                    width: 60,
                                    text: '刷新'
                                }
                            ]
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












