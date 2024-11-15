const express = require("express");
const router = express.Router();

const path = require("path");

router.use((req, res, next) => {
    const fileRoutes = ["/gemma2b", "/falcon_rw1b", "/stable_lm3b", "/phi2"];
    const currentDateTime = new Date().toLocaleString();

    if(fileRoutes.includes(req.path)) {
        console.log(`[${currentDateTime}] Download request received (${req.ip})`);
    }

    next();
});

router.get("/gemma2b", (req, res) => {
    const filePath = path.join(__dirname, "..", "..", "ringo_cloud_storage", "download", "gemma2b.bin");
    
    res.setHeader("Content-Disposition", 'attachment; filename="gemma2b.bin"');
    res.set("Content-Type", "application/octet-stream");
    res.sendFile(filePath);
});

router.get("/falcon_rw1b", (req, res) => {
    const filePath = path.join(__dirname, "..", "..", "ringo_cloud_storage", "download", "falcon_rw1b.bin");
    
    res.setHeader("Content-Disposition", 'attachment; filename="falcon_rw1b.bin"');
    res.set("Content-Type", "application/octet-stream");
    res.sendFile(filePath);
});

router.get("/stable_lm3b", (req, res) => {
    const filePath = path.join(__dirname, "..", "..", "ringo_cloud_storage", "download", "stable_lm3b.bin");
    
    res.setHeader("Content-Disposition", 'attachment; filename="stable_lm3b.bin"');
    res.set("Content-Type", "application/octet-stream");
    res.sendFile(filePath);
});

router.get("/phi2", (req, res) => {
    const filePath = path.join(__dirname, "..", "..", "ringo_cloud_storage", "download", "phi2.bin");
    
    res.setHeader("Content-Disposition", 'attachment; filename="phi2.bin"');
    res.set("Content-Type", "application/octet-stream");
    res.sendFile(filePath);
});

module.exports = router;