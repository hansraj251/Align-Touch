require("dotenv").config();

const pool =
    require("./db");

async function testConnection() {

    const result =
        await pool.query(
            "SELECT NOW() AS current_time"
        );

    console.log(
        "PostgreSQL connected:",
        result.rows[0]
    );

    await pool.end();
}

testConnection()
    .catch(
        (error) => {

            console.error(
                "PostgreSQL connection failed:",
                error
            );

            process.exit(1);
        }
    );
