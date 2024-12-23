import React from "react";
import ReactDOM from "react-dom";
import { SWRConfig } from "swr";

import App from "./App";

import { fetcher } from "./utils";

import "./index.css";

ReactDOM.render(
  <React.StrictMode>
    <SWRConfig value={{ fetcher }}>
      <App />
    </SWRConfig>
  </React.StrictMode>,
  document.getElementById("root"),
);
