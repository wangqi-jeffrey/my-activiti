/* global Ext */

Ext.define('MyApp.view.flow.ActModelGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.actmodelgrid',
    rowLines: true,
    columnLines: true,
    title:'模型管理',
    config: {idName: 'id'},
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'id',
                    align: 'center',
                    flex: 1,
                    text: '编号'
                },
                {
                    xtype: 'gridcolumn',
                    align: 'center',
                    dataIndex: 'name',
                    flex: 1,
                    text: '名称',
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
                    dataIndex: 'metaInfo',
                    align: 'center',
                    flex: 1,
                    text: '描述',
                     renderer: function (value, metaData, data) {
                        if (value) {
                            var description = eval('(' + value + ')').description;
                            return description;
                        }
                    }
                },
                {
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i',
                    dataIndex: 'createTime',
                    align: 'center',
                    text: '创建时间',
                    flex: 1,
                    renderer: function (value) {
                        if (value === null || value === 0) {
                            return '';
                        } else {
                            return Ext.util.Format.date(new Date(parseInt(value)), 'Y-m-d H:i');
                        }
                    }
                },
                {
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i',
                    dataIndex: 'lastUpdateTime',
                    align: 'center',
                    text: '最后修改时间',
                    flex: 1,
                    renderer: function (value) {
                        if (value === null || value === 0) {
                            return '';
                        } else {
                            return Ext.util.Format.date(new Date(parseInt(value)), 'Y-m-d H:i');
                        }
                    }
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
                                this.up('grid').fireEvent('onActmodelgridActioncolumnPicClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-xml',
                            tooltip: '查看所选模型流程XML',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActmodelgridActioncolumnXmlClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    text: '操作',
                    align: 'center',
                    flex: 1,
                    editor: {xtype: 'label'}, //加上这行是因为同时使用actioncolumn和rowediting时有bug,双击或单击编辑数据会报错
                    items: [
                        {
                            iconCls: 'icon-deploy',
                            tooltip: '部署所选模型',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActmodelgridActioncolumnDeployClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-design',
                            tooltip: '对所选模型进行流程设计',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActmodelgridActioncolumnEditClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        },
                        {
                            iconCls: 'icon-download',
                            tooltip: '导出模型XML文件',
                            handler: function (view, rowIndex, colIndex, item, e, record, row) {
                                this.up('grid').fireEvent('onActmodelgridActioncolumnExportClick', view, rowIndex, colIndex, item, e, record, row);
                            }
                        }
                    ]
                }
                
            ],
            viewConfig: {
                enableTextSelection: true,
                loadMask: {msg: '正在加载数据...'}
            },
            plugins: {
                ptype: 'rowediting',
                saveBtnText: '确定',
                cancelBtnText: '取消'
            },
            selModel: {
                selType: 'checkboxmodel',
                mode: 'SIMPLE',
                checkOnly: true
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    dock: 'bottom',
                    displayInfo: true,
                    items: ['->'],
                    prependButtons: true
                },
                {
                    xtype: 'toolbar',
                    dock: 'top',
                    items: [
                        {
                            xtype: 'form',
                            layout: 'fit',
                            border: false,
                            width: 80,
                            items: [
                                {
                                    xtype: 'filefield',
                                    name: 'file',
                                    buttonOnly: true,
                                    buttonConfig: {
                                        width: 80,
                                        text: '导入模型'
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'button',
                            iconCls: 'icon-add',
                            width: 80,
                            action: 'add',
                            text: '新增'
                        },
                        {
                            xtype: 'button',
                            iconCls: 'icon-delete',
                            action: 'delete',
                            width: 80,
                            text: '删除'
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
                                    emptyText: '输入查询关键字'
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
                }
            ]
        });

        me.callParent(arguments);
    }


});
