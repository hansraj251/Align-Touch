require("dotenv").config();

const userService =
    require("./userService");

const pool =
    require("../database/db");

async function test() {

    const result =
        await userService.login({
            phone: "9876543210",
            password: "test123456"
        });

    console.log(
        "Login successful:"
    );

    console.log(
        "User:",
        result.user
    );

    console.log(
        "JWT:",
        result.token
    );

    await pool.end();
}

test()
    .catch(
        async (error) => {

            console.error(
                "Login test failed:",
                error.message
            );

            await pool.end();

            process.exit(1);
        }
    );
