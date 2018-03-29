/* global Ext, eval */

Ext.define('MyApp.controller.flow.FlowController', {
    extend: 'Ext.app.Controller',
    init: function (application) {
        this.control({
            actmodelgrid: {
                //部署模型
                onActmodelgridActioncolumnDeployClick: this.onActmodelgridActioncolumnDeployClick,
                //查看模型定义的流程图
                onActmodelgridActioncolumnPicClick: this.onActmodelgridActioncolumnPicClick,
                //查看模型定义的定义内容
                onActmodelgridActioncolumnXmlClick: this.onActmodelgridActioncolumnXmlClick,
                //对模型定义流程
                onActmodelgridActioncolumnEditClick: this.onActmodelgridActioncolumnEditClick,
                //下载模型定义文件
                onActmodelgridActioncolumnExportClick: this.onActmodelgridActioncolumnExportClick,
                //编辑模型表单
                edit: this.onActmodelgridEdit,
                boxready: this.onActmodelgridBoxready
            },
            'actmodelgrid button[action=add]': {
                //新增模型
                click: this.onActmodelgridAddButtonClick
            },
            'actmodelgrid button[action=delete]': {
                //删除模型
                click: this.onActmodelgridDeleteButtonClick
            },
            'actmodelgrid button[action=search]': {
                //查询模型
                click: this.onActmodelgridSearchButtonClick
            },
            'actmodelgrid button[action=refresh]': {
                //刷新模型数据
                click: this.onActmodelgridRefreshButtonClick
            },
            'actmodelgrid filefield[name=file]': {
                //导入模型文件
                change: this.onActmodelgridFilefieldChange
            },
            'actmodelwindow button[action=submit]': {
                //提交模型表单
                click: this.onActmodelwindowSubmitButtonClick

            },
            actprocessdefinitiongrid: {
                //编辑流程定义表单
                edit: this.onActprocessdefinitiongridEdit,
                //查看流程图
                onActprocessdefinitiongridActioncolumnPicClick: this.onActprocessdefinitiongridActioncolumnPicClick,
                //查看流程定义文件内容
                onActprocessdefinitiongridActioncolumnXmlClick: this.onActprocessdefinitiongridActioncolumnXmlClick,
                //下载流程定义文件
                onActprocessdefinitiongridActioncolumnDownloadClick: this.onActprocessdefinitiongridActioncolumnDownloadClick,
                //改变流程定义状态 激活或挂起
                onActprocessdefinitiongridActioncolumnStateClick: this.onActprocessdefinitiongridActioncolumnStateClick,
                //将流程定义转换为模型
                onActprocessdefinitiongridActioncolumnConvertClick: this.onActprocessdefinitiongridActioncolumnConvertClick,
                //删除流程定义文件
                onActprocessdefinitiongridActioncolumnDeleteClick: this.onActprocessdefinitiongridActioncolumnDeleteClick,
                //启动流程
                onActprocessdefinitiongridActioncolumnStartClick: this.onActprocessdefinitiongridActioncolumnStartClick,
                boxready: this.onActprocessdefinitiongridBoxready
            },
            'actprocessdefinitiongrid button[action=search]': {
                //查询流程定义
                click: this.onActprocessdefinitiongridSearchButtonClick
            },
            'actprocessdefinitiongrid button[action=refresh]': {
                //刷新流程定义数据
                click: this.onActprocessdefinitiongridRefreshButtonClick
            },
            'actprocessdefinitiongrid filefield[name=file]': {
                //部署外部流程定义文件
                change: this.onActprocessdefinitiongridFilefieldChange
            },
            acthistoricprocessinstancegrid: {
                boxready: this.onActhistoricprocessinstancegridBoxready,
                onActhistoricprocessinstancegridActioncolumnPicClick: this.onActhistoricprocessinstancegridActioncolumnPicClick,
                onActhistoricprocessinstancegridActioncolumnDeleteClick: this.onActhistoricprocessinstancegridActioncolumnDeleteClick
            },
            'acthistoricprocessinstancegrid button[action=delete]': {
                //删除流程实例
                click: this.onActhistoricprocessinstancegridDeleteButtonClick
            }

        });
    },
    onActhistoricprocessinstancegridDeleteButtonClick: function (btn) {//删除流程实例
        var grid = btn.up('grid');
        var idName = grid.getIdName();
        var models = grid.getSelectionModel().getSelection();
        var store = grid.getStore();
        var url = store.getProxy().api['deleteByIds'];
        if (models.length > 0) {
            var ids = [];
            Ext.Array.forEach(models, function (model) {
                ids.push(model.get(idName));
            });
            if (ids.length > 0) {
                Ext.Msg.show({
                    title: '提示',
                    msg: '是否删除所选数据?',
                    modal: true,
                    buttons: Ext.Msg.YESNO,
                    icon: Ext.Msg.QUESTION,
                    fn: function (id) {
                        if (id === 'yes') {
                            var myMask = new Ext.LoadMask({target: grid, msg: '数据正在删除,请等候......', removeMask: true});
                            myMask.show();
                            Ext.Ajax.request({
                                url: url,
                                method: 'POST',
                                params: {ids: ids},
                                success: function (response) {
                                    myMask.destroy();
                                    var msg = eval('(' + response.responseText + ')').msg;
                                    if (msg) {
                                        if (msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                                            store.loadPage(1);
                                            Ext.example.msg('提示', msg);
                                        }
                                    }

                                },
                                failure: function (response, opts) {
                                    myMask.destroy();
                                    Ext.example.msg('提示', '数据删除失败,请稍后再试');
                                }
                            });
                        }
                    }
                });
            }
        } else {
            Ext.example.msg('提示', '请至少选择一条要删除的数据');
        }

    },
    onActhistoricprocessinstancegridActioncolumnPicClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var processDefinitionId = record.get('processDefinitionId');
        var processInstanceId = record.get('id');

        var url = Ext.String.format('diagram-viewer/index.html?processDefinitionId={0}&processInstanceId={1}',
                processDefinitionId, processInstanceId);
        var endTime = record.get('endTime');
        if (endTime) {
            url = Ext.String.format('flow/traceProcess.do?processInstanceId={0}', record.get('id'));
        }
        window.open(url, '_blank');
    },
    onActhistoricprocessinstancegridBoxready: function (grid) {
        var actprocessdefinitionstore = Ext.createByAlias('store.acthistoricprocessinstancestore');
        grid.down('pagingtoolbar').bindStore(actprocessdefinitionstore);
        grid.bindStore(actprocessdefinitionstore);
        actprocessdefinitionstore.load();
    },
    onActmodelwindowSubmitButtonClick: function (btn) {
        var win = btn.up('window');
        var actmodelgrid = Ext.ComponentQuery.query('actmodelgrid')[0];
        var form = win.down('form').getForm();
        var store = actmodelgrid.getStore();
        var url = store.getProxy().api['createModel'];
        var myMask = new Ext.LoadMask({target: win, msg: '正在进行数据操作,请等待......', removeMask: true});
        myMask.show();

        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: form.getValues(),
            async: false,
            success: function (response) {
                myMask.destroy();
                var response = eval('(' + response.responseText + ')');
                var id = response.id;
                win.close();
                store.load();
                var url = 'modeler.html?modelId=' + id;
                window.open(url, '_blank');

            },
            failure: function (response, opts) {
                myMask.destroy();
                var msg = eval('(' + response.responseText + ')').msg;
                if (msg) {
                    if (msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                        Ext.example.msg('提示', msg);
                    }
                }
            }
        });

    },
    onActmodelgridFilefieldChange: function (filefield, value) {
        var grid = filefield.up('grid');
        var actmodelstore = grid.getStore();
        var form = filefield.up('form').getForm();
        if (value.split('\\').length > 0)
            value = value.split('\\')[value.split('\\').length - 1];
        if (value.indexOf(".bpmn") > 0 || value.indexOf(".xml") > 0) {
            var url = actmodelstore.getProxy().api['importModel'];
            var myMask = new Ext.LoadMask(grid, {msg: '操作正在进行,请等待......', removeMask: true});
            myMask.show();
            form.submit({
                url: url,
                method: 'POST',
                success: function (form, action) {
                    myMask.destroy();
                    actmodelstore.load();
                    Ext.example.msg('提示', action.result.msg);
                },
                failure: function (form, action) {
                    myMask.destroy();
                    if (action.result) {
                        var msg = action.result.msg;
                        if (msg && msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                            Ext.example.msg('提示', msg);
                        }
                    }
                }
            });

        } else {
            Ext.example.msg('提示', '只支持上传后缀名为bpmn、xml的文件。');
        }

    },
    onActmodelgridSearchButtonClick: function (btn) {
        var grid = btn.up('grid');
        var name = grid.down('[name=name]').getValue();
        var store = grid.getStore();
        store.proxy.extraParams = {name: name};
        store.loadPage(1);
    },
    onActmodelgridRefreshButtonClick: function (btn) {
        var grid = btn.up('grid');
        grid.down('[name=name]').reset();
        var store = grid.getStore();
        store.proxy.extraParams = {};
        store.loadPage(1);
    },
    onActmodelgridDeleteButtonClick: function (btn) {
        var grid = btn.up('grid');
        var idName = grid.getIdName();
        var models = grid.getSelectionModel().getSelection();
        var store = grid.getStore();
        var url = store.getProxy().api['deleteByIds'];
        if (models.length > 0) {
            var ids = [];
            Ext.Array.forEach(models, function (model) {
                ids.push(model.get(idName));
            });
            if (ids.length > 0) {
                Ext.Msg.show({
                    title: '提示',
                    msg: '是否删除所选数据?',
                    modal: true,
                    buttons: Ext.Msg.YESNO,
                    icon: Ext.Msg.QUESTION,
                    fn: function (id) {
                        if (id === 'yes') {
                            var myMask = new Ext.LoadMask({target: grid, msg: '数据正在删除,请等候......', removeMask: true});
                            myMask.show();
                            Ext.Ajax.request({
                                url: url,
                                method: 'POST',
                                params: {ids: ids},
                                success: function (response) {
                                    myMask.destroy();
                                    var msg = eval('(' + response.responseText + ')').msg;
                                    if (msg) {
                                        if (msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                                            store.loadPage(1);
                                            Ext.example.msg('提示', msg);
                                        }
                                    }

                                },
                                failure: function (response, opts) {
                                    myMask.destroy();
                                    Ext.example.msg('提示', '数据删除失败,请稍后再试');
                                }
                            });
                        }
                    }
                });
            }
        } else {
            Ext.example.msg('提示', '请至少选择一条要删除的数据');
        }
    },
    onActmodelgridAddButtonClick: function (btn) {
        var grid = btn.up('grid');
        var actmodelwindow = Ext.ComponentQuery.query('actmodelwindow')[0];
        if (!actmodelwindow)
            actmodelwindow = Ext.widget('actmodelwindow');
        actmodelwindow.show();
        actmodelwindow.setGrid(grid.getXType());
    },
    onActmodelgridBoxready: function (grid) {
        var actmodelstore = Ext.createByAlias('store.actmodelstore');
        grid.down('pagingtoolbar').bindStore(actmodelstore);
        grid.bindStore(actmodelstore);
        actmodelstore.load();
    },
    onActmodelgridEdit: function (event, edit) {
        var store = edit.store;
        var params = edit.record.getData();
        Ext.Ajax.request({
            url: store.getProxy().api['updateName'],
            params: params,
            method: 'POST',
            success: function (response) {
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
                store.load();
            }
        });
    },
    onActmodelgridActioncolumnExportClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var id = record.get('id');
        var store = view.getStore();
        var url = store.getProxy().api['exportById'] + '?id=' + id;
        window.open(url, '_blank');
    },
    onActmodelgridActioncolumnEditClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var url = Ext.String.format('modeler.html?modelId={0}', record.get('id'));
        window.open(url, '_blank');
    },
    onActmodelgridActioncolumnDeployClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var id = record.get('id');
        var store = view.getStore();
        var url = store.getProxy().api['deployById'];
        var myMask = new Ext.LoadMask(view, {msg: '操作正在进行,请等待......', removeMask: true});
        myMask.show();
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {id: id},
            success: function (response) {
                myMask.destroy();
                store.load();
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
            },
            failure: function (response, opts) {
                myMask.destroy();
                Ext.example.msg('提示', '数据操作失败,请稍后再试');
            }
        });
    },
    onActmodelgridActioncolumnPicClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var id = record.get('id');
        var store = view.getStore();
        var url = store.getProxy().api['showPic'] + '?id=' + id;
        window.open(url, '_blank');
    },
    onActmodelgridActioncolumnXmlClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var id = record.get('id');
        var store = view.getStore();
        var url = store.getProxy().api['showXML'] + '?id=' + id;
        window.open(url, '_blank');
    },
    onActprocessdefinitiongridRefreshButtonClick: function (btn) {
        var grid = btn.up('grid');
        grid.down('[name=name]').reset();
        var store = grid.getStore();
        store.proxy.extraParams = {};
        store.loadPage(1);
    },
    onActprocessdefinitiongridSearchButtonClick: function (btn) {
        var grid = btn.up('grid');
        var name = grid.down('[name=name]').getValue();
        var store = grid.getStore();
        store.proxy.extraParams = {name: name};
        store.loadPage(1);
    },
    onActprocessdefinitiongridFilefieldChange: function (filefield, value) {
        var grid = filefield.up('grid');
        var store = grid.getStore();
        var form = filefield.up('form').getForm();
        if (value.split('\\').length > 0)
            value = value.split('\\')[value.split('\\').length - 1];
        if (value.indexOf(".zip") > 0 || value.indexOf(".bpmn") > 0 || value.indexOf(".xml") > 0) {
            var url = store.getProxy().api['deployByFile'];
            var myMask = new Ext.LoadMask(grid, {msg: '操作正在进行,请等待......', removeMask: true});
            myMask.show();
            form.submit({
                url: url,
                method: 'POST',
                success: function (form, action) {
                    myMask.destroy();
                    store.load();
                    Ext.example.msg('提示', action.result.msg);
                },
                failure: function (form, action) {
                    myMask.destroy();
                    if (action.result) {
                        var msg = action.result.msg;
                        if (msg && msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                            Ext.example.msg('提示', msg);
                        }
                    }
                }
            });

        } else {
            Ext.example.msg('提示', '只支持上传后缀名为zip、xml、bpmn的文件。');
        }

    },
    onActprocessdefinitiongridBoxready: function (grid) {
        var actprocessdefinitionstore = Ext.createByAlias('store.actprocessdefinitionstore');
        grid.down('pagingtoolbar').bindStore(actprocessdefinitionstore);
        grid.bindStore(actprocessdefinitionstore);
        actprocessdefinitionstore.load();
    },
    onActprocessdefinitiongridActioncolumnDownloadClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = view.getStore();
        var url = Ext.String.format(store.getProxy().api['exportByProcessDefinitionId'] + '?processDefinitionId={0}&resourceName={1}',
                record.get('id'), record.get('resourceName'));
        window.open(url, '_blank');
    },
    onActprocessdefinitiongridActioncolumnDeleteClick: function (view, rowIndex, colIndex, item, e, record, row) {//删除流程
        var grid = view.up('grid');
        var modelId = record.get('deploymentId');
        var store = grid.getStore();
        var url = store.getProxy().api['deleteByIds'];
        Ext.Msg.show({
            title: '提示',
            msg: '是否删除所选数据?',
            modal: true,
            buttons: Ext.Msg.YESNO,
            icon: Ext.Msg.QUESTION,
            fn: function (id) {
                if (id === 'yes') {
                    var myMask = new Ext.LoadMask({target: grid, msg: '操作正在进行,请等待......', removeMask: true});
                    myMask.show();
                    Ext.Ajax.request({
                        url: url,
                        method: 'POST',
                        params: {ids: modelId},
                        success: function (response) {
                            myMask.destroy();
                            var msg = eval('(' + response.responseText + ')').msg;
                            if (msg) {
                                if (msg.indexOf("超时") < 0 && msg.indexOf("权限") < 0) {
                                    store.load();
                                    Ext.example.msg('提示', msg);
                                }
                            }

                        },
                        failure: function (response, opts) {
                            myMask.destroy();
                            Ext.example.msg('提示', '数据操作失败,请稍后再试');
                        }
                    });
                }
            }
        });

    },
    onActprocessdefinitiongridActioncolumnConvertClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var processDefinitionId = record.get('id');
        var store = view.getStore();
        var url = store.getProxy().api['convertToModel'];
        var myMask = new Ext.LoadMask(view, {msg: '操作正在进行,请等待......', removeMask: true});
        myMask.show();
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {processDefinitionId: processDefinitionId},
            success: function (response) {
                myMask.destroy();
                store.load();
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
            },
            failure: function (response, opts) {
                myMask.destroy();
                Ext.example.msg('提示', '数据操作失败,请稍后再试');
            }
        });
    },
    onActprocessdefinitiongridActioncolumnStateClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = view.getStore();
        var url = store.getProxy().api['updateState'];
        var state = 'active';
        if (!record.get('suspended'))
            state = 'suspend';
        var processDefinitionId = record.get('id');
        var myMask = new Ext.LoadMask(view, {msg: '操作正在进行,请等待......', removeMask: true});
        myMask.show();
        Ext.Ajax.request({
            url: url,
            method: 'POST',
            params: {state: state, processDefinitionId: processDefinitionId},
            success: function (response) {
                myMask.destroy();
                store.load();
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
            },
            failure: function (response, opts) {
                myMask.destroy();
                Ext.example.msg('提示', '数据操作失败,请稍后再试');
            }
        });
    },
    onActprocessdefinitiongridActioncolumnStartClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var grid = view.up('grid');
        var userId = 'dang';
        var businessKey = Ext.data.IdGenerator.get('uuid').generate();
        var keyName = record.get('keyName');
        var myMask = new Ext.LoadMask({target: grid, msg: '操作正在进行,请等待......', removeMask: true});
        myMask.show();
        Ext.Ajax.request({
            url: 'flow/startFlow.do',
            params: {userId: userId, businessKey: businessKey, keyName: keyName},
            method: 'POST',
            success: function (response) {
                myMask.destroy();
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
            }
        });
    },
    onActprocessdefinitiongridActioncolumnPicClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var url = Ext.String.format('flow/resourceRead.do?processDefinitionId={0}&resourceName={1}', record.get('id'), record.get('diagramResourceName'));
        window.open(url, '_blank');
    },
    onActprocessdefinitiongridActioncolumnXmlClick: function (view, rowIndex, colIndex, item, e, record, row) {
        var url = Ext.String.format('flow/resourceRead.do?processDefinitionId={0}&resourceName={1}', record.get('id'), record.get('resourceName'));
        window.open(url, '_blank');
    },
    onActprocessdefinitiongridEdit: function (event, edit) {
        var store = edit.store;
        var params = edit.record.getData();
        Ext.Ajax.request({
            url: store.getProxy().api['updateName'],
            params: params,
            method: 'POST',
            success: function (response) {
                var msg = eval('(' + response.responseText + ')').msg;
                Ext.example.msg('提示', msg);
                store.load();
            }
        });
    },
    views: [
        'flow.ActModelGrid',
        'flow.ActModelWindow',
        'flow.ActProcessDefinitionGrid',
        'flow.ActHistoricProcessInstanceGrid'
    ],
    stores: [
        'flow.ActModelStore',
        'flow.ActProcessDefinitionStore',
        'flow.ActHistoricProcessInstanceStore'
    ],
    models: [
        //引入一个或多个模型文件,如: 'systemmanage.OrganizationTreeModel'   
    ]
});
