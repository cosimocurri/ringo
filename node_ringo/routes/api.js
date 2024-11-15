const express = require("express");
const router = express.Router();

//ROUTERS
const downloadRouter = require("./api_routes/download");

router.use("/download", downloadRouter);

module.exports = router;