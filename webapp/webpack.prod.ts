import merge from "webpack-merge"
import commonConfig from "./webpack.config"

const prodConfig = merge(commonConfig, {
    mode: "production",
    performance: {
        maxAssetSize: 500000,
        maxEntrypointSize: 500000,
    },
    optimization:{
        usedExports: true
    }
})

export default prodConfig
