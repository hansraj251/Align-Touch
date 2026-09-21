require("dotenv").config();

const userService =
    require("./userService");

const pool =
    require("../database/db");

async function test() {

    const uniquePhone =
        "999" +
        Date.now().toString().slice(-7);

    const user =
        await userService.register({
            phone: uniquePhone,
            email: null,
            password: "test123456",
            displayName: "Test User"
        });

    console.log(
        "User created:",
        user
    );

    await pool.end();
}

test()
    .catch(
        async (error) => {

            console.error(
                "User service test failed:",
                error.message
            );

            await pool.end();

            process.exit(1);
        }
    );
