module.exports = {
    runtimeCompiler: true,
    chainWebpack: (config) => {
        config
            .plugin('html')
            .tap(args => {
                args[0].title = 'TFE Web Admin';
                return args;
            });
    },
    "transpileDependencies": [
        "vuetify"
    ]
}
