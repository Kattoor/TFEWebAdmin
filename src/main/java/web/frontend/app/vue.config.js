module.exports = {
    chainWebpack: (config) => {
        config
            .plugin('html')
            .tap(args => {
                args[0].title = 'TFE Web Admin';
                return args;
            });
    }
}
