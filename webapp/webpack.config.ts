import HtmlWebpackPlugin from "html-webpack-plugin"
import path from "path"
import * as webpack from "webpack"

const config: webpack.Configuration = {
    entry: "./index.js",
    output: {
        filename: "bundle.[hash].js",
        path: path.resolve(__dirname, "dist")
    },
    devServer: {
        historyApiFallback: true,
        port: 8080,
        proxy: {
            "/api": {
                target: "http://localhost:9000",
                secure: false
            }
        }
    },
    plugins: [
        new HtmlWebpackPlugin({
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
                test: /\.jsx?$/,
                exclude: /node_modules/,
                loader: require.resolve("babel-loader")
            },
            {
                test: /\.tsx?$/,
                use: "ts-loader",
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: ["style-loader", "css-loader"]
            },
            {
                test: /\.s[ac]ss$/,
                use: ["style-loader", "css-loader", "sass-loader"]
            },
            {
                test: /\.(png|svg|jpg|gif)$/,
                use: ["file-loader"]
            }
        ]
    }
}

export default config
