import HtmlWebpackPlugin from "html-webpack-plugin"
import {Configuration} from "webpack"
import {resolve} from "path"

const commonConfig: Configuration = {
    entry: "./user-area.tsx",
    output: {
        path: resolve(__dirname, "dist"),
        filename: "index_bundle.js",
        publicPath: "/",
    },
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
                test: /\.(png|svg|jpg|gif)$/,
                use: ["file-loader"]
            },
            {
                test: /\.(woff(2)?|ttf|eot|svg)(\?v=\d+\.\d+\.\d+)?$/,
                use: [
                    {
                        loader: "file-loader",
                        options: {
                            name: "[name].[ext]",
                            outputPath: "fonts/"
                        }
                    }
                ]
            }
        ]
    }
}

export default commonConfig
