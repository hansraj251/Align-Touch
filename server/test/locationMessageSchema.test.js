const test = require("node:test");

const assert = require("node:assert/strict");

const fs = require("fs");

test("messages schema contains location coordinates", () => {
    const schema =
        fs.readFileSync(
            "server/database/schema.sql",
            "utf8"
        );

    assert.match(
        schema,
        /latitude\s+(DOUBLE PRECISION|NUMERIC|DECIMAL)/i
    );

    assert.match(
        schema,
        /longitude\s+(DOUBLE PRECISION|NUMERIC|DECIMAL)/i
    );
});
