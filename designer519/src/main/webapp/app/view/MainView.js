Ext.define('MyApp.view.MainView', {
    extend: 'Ext.container.Viewport',
    alias: 'widget.mainview',
    layout: 'border',
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            items: [{
                    xtype: 'panel',
                    itemId: 'topPanel',
                    region: 'north', // 顶部
                    html: '<p style="color: white; font-size: 30px;font-weight: bold;font-family:微软简综艺;margin-left: 100px;margin-top: 35px">Activiti 5.19 流程设计器</p>',
                    height: 100,
                    bodyStyle: {
                        background: '#3498db'
                    }
                },
                {
                    xtype: 'tabpanel',
                    region: 'center',
                    items:[
                        {xtype:'actmodelgrid'},
                        {xtype:'actprocessdefinitiongrid'},
                        {xtype:'acthistoricprocessinstancegrid'}
                    ]


                }, {
                    xtype: 'toolbar',
                    region: 'south', // 底部
                    height: 30,
                    border: 1,
                    style: {
                        borderStyle: 'none',
                        'background-color': '#3498db'
                    },
                    layout: {
                        pack: 'center',
                        type: 'hbox'
                    },
                    items: [{
                            xtype: 'label',
                            html: '程序开发：党福生&nbsp;&nbsp;&nbsp email:150433693@qq.com',
                            style: {
                                'font-weight': 'bold',
                                'font-size': '13px',
                                'color': '#FFFFFF'
                            }
                        }]
                }]
        });

        me.callParent(arguments);
    }
});