import {merge} from "webpack-merge"
import commonConfig from "./webpack.config"

const devConfig = merge(commonConfig, {
    mode: "development",
    devServer: {
        contentBase: "./",
        historyApiFallback: true,
        port: 8080,
        proxy: {
            "/api": {
                target: "http://localhost:9000",
                secure: false
            }
        }
    }
})

export default devConfig