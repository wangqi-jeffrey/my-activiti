/* global Ext */

Ext.define('MyApp.view.flow.ActModelWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.actmodelwindow',
    title: '模型信息',
    modal: true,
    layout: {
        type: 'fit'
    },
    config: {grid: null},
    initComponent: function () {
        var me = this;
        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'form',
                    autoScroll: true,
                    fieldDefaults: {
                        margin: 13,
                        labelAlign: 'right',
                        labelStyle: 'font-weight:bold',
                        width: 350,
                        labelWidth: 90
                    },
                    buttonAlign: 'center',
                    buttons: [
                        {
                            xtype: 'button',
                            text: '提交',
                            formBind: true,
                            action: 'submit'
                        }
                    ],
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: '名称',
                            afterLabelTextTpl: '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>',
                            allowBlank: false,
                            name: 'name'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'key',
                            afterLabelTextTpl: '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>',
                            allowBlank: false,
                            name: 'key'
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: '描述',
                            name: 'description'
                        }
                    ]
                }

            ]
        });

        me.callParent(arguments);
    }
           
});
