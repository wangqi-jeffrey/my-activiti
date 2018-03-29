/* global Ext */

Ext.define('MyApp.store.flow.ActProcessDefinitionStore', {
    extend: 'Ext.data.Store',
    alias: 'store.actprocessdefinitionstore',
    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
                storeId: 'flow.ActProcessDefinitionStore',
                  fields:['id','deploymentId','description','category','diagramResourceName','resourceName','keyName','name',
                'version','deploymentTime','suspended'],
                sorters: [{property: 'deploymentTime'}],
                proxy: {
                    actionMethods: {read: 'POST'}, //这个很重要！防止参数乱码
                    api: {
                       find: 'flow/findProcessDefinition.do',
                        updateState: 'flow/updateState.do',//改变流程状态
                         updateName: 'flow/updateProcessDefinitionName.do',//改变流程定义的名称
                        convertToModel:'flow/convertToModel.do',//流程定义转换为模型(创建新模型)
                        deployByFile: 'flow/deployByFile.do',
                        deleteByIds: 'flow/deleteProcessDefinition.do',
                        exportByProcessDefinitionId: 'flow/exportByProcessDefinitionId.do'
                    },
                    type: 'ajax',
                    url: 'flow/findProcessDefinition.do',
                     reader: {root: "datas"}
                }
            }, cfg)]);
    }
});