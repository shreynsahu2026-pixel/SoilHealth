const signupForm = document.getElementById("signupForm");

signupForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return;
    }

    try {

        const response = await fetch("http://localhost:8080/api/signup", {

            method: "POST",

            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },

            body: new URLSearchParams({
                username: username,
                email: email,
                password: password
            })

        });

        const result = await response.json();

        if (response.ok) {

            alert("Account created successfully!");

            window.location.href = "index.html";

        } else {

            alert(result.message || "Registration failed.");

        }

    } catch (error) {

        alert("Could not connect to the server.");

        console.error(error);

    }

});