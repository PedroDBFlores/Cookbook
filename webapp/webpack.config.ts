import HtmlWebpackPlugin from "html-webpack-plugin"
import * as webpack from "webpack"

const commonConfig: webpack.Configuration = {
    entry: "./index.js",
    plugins: [
        new HtmlWebpackPlugin({
            filename: "./index.html",
            template: "./index.html"
        })
    ],
    resolve: {
        modules: [__dirname, "src", "node_modules"],
        extensions: ["*", ".js", ".jsx", ".tsx", ".ts"],
    },
    module: {
        rules: [
            {
                test: /\.js|\.ts|\.tsx$/,
                use: "ts-loader",
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            },
            {
                test: /\.(png|svg|jpg|gif)$/,
                use: ["file-loader"]
            }
        ]
    }
}

export default commonConfig
