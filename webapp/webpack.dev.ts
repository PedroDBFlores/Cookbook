import merge from "webpack-merge"
import commonConfig from "./webpack.config"
import {Configuration} from "webpack"
// import {BundleAnalyzerPlugin} from "webpack-bundle-analyzer"

const devConfig = merge(commonConfig, {
    mode: "development",
    devServer: {
        contentBase: "./",
        historyApiFallback: true,
        port: 8080,
        proxy: {
            "/api": "http://localhost:9000"
        }
    },
} as Configuration)

export default devConfig
