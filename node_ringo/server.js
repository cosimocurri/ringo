const express = require("express");
const app = express();
const port = 3000;

//ROUTERS
const apiRouter = require("./routes/api");

app.use("/api", apiRouter);

app.listen(port, () => {
    console.log(`SERVER: listening on port ${port}`);
});