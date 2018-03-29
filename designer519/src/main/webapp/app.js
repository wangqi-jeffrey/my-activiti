Ext.Loader.setConfig({enabled: true});
Ext.Loader.loadScript({url: 'ext/locale/ext-lang-zh_CN.js'});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'MyApp',
    paths: {'Ext.ux': 'ext/examples/ux'},
    requires: [

    ],
    views: [
        'MainView'

    ],
    controllers: [
'flow.FlowController'
    ],
    stores: [

    ],
    models: [

    ],
    launch: function () {
        Ext.create('MyApp.view.MainView');

    }
});
