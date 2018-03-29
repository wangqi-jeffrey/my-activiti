/* global Ext */

Ext.define('MyApp.store.flow.ActHistoricProcessInstanceStore', {
    extend: 'Ext.data.Store',
    alias: 'store.acthistoricprocessinstancestore',
    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
                storeId: 'flow.ActHistoricProcessInstanceStore',
                 fields:['id','name','startTime','startActivityId','processVariables','processDefinitionId','endTime','durationInMillis',
                'businessKey','deleteReason','startUserId','superProcessInstanceId','tenantId'],
                sorters: [{property: 'createTime'}],
                proxy: {
                    actionMethods: {read: 'POST'}, //这个很重要！防止参数乱码
                    api: {
                        find: 'flow/findHistoricProcessInstance.do',
                        deleteByIds: 'flow/deleteProcessInstance.do'
                    },
                    type: 'ajax',
                    url: 'flow/findHistoricProcessInstance.do',
                     reader: {root: "datas"}
                }
            }, cfg)]);
    }
});