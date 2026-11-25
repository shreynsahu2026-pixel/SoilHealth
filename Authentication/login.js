const loginForm = document.getElementById("loginForm");

loginForm.addEventListener("submit", async function(event) {

    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {

        const response = await fetch("http://localhost:8080/api/login", {
            method: "POST",

            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },

            body: new URLSearchParams({
                email: email,
                password: password
            })
        });

        const result = await response.json();

        if (response.ok) {

            alert("Login successful!");

            window.location.href = "../website/index.html";

        } else {

            alert(result.message || "Invalid email or password.");

        }

    } catch (error) {

        alert("Could not connect to the server.");
        console.error(error);

    }

});