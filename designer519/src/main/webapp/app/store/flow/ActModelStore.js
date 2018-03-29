/* global Ext */

Ext.define('MyApp.store.flow.ActModelStore', {
    extend: 'Ext.data.Store',
    alias: 'store.actmodelstore',
    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
                storeId: 'flow.ActModelStore',
                fields: ['id', 'name', 'type', 'key', 'category', 'createTime', 'lastUpdateTime', 'version', 'metaInfo', 'deploymentId'],
                sorters: [{property: 'createTime'}],
                proxy: {
                    actionMethods: {read: 'POST'}, //这个很重要！防止参数乱码
                    api: {
                        find: 'flow/findModel.do',
                        deployById: 'flow/deployByModelId.do',
                        exportById: 'flow/exportByModelId.do',
                        deleteByIds: 'flow/deleteModel.do',
                        createModel: 'flow/createModel.do',
                        importModel: 'flow/importModel.do',
                        showPic: 'flow/showPicByModelId.do',
                        showXML: 'flow/showXMLByModelId.do',
                        updateName: 'flow/updateModelName.do'//改变模型的名称
                    },
                    type: 'ajax',
                    url: 'flow/findModel.do',
                    reader: {root: "datas"}
                }
            }, cfg)]);
    }
});