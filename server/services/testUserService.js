require("dotenv").config();

const userService =
    require("./userService");

const pool =
    require("../database/db");

async function test() {

    const uniquePhone =
        "999" +
        Date.now().toString().slice(-7);

    const result =
        await userService.loginWithOtp({

            identifier:
                uniquePhone

        });

    console.log(
        "User login successful:"
    );

    console.log(
        result
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
