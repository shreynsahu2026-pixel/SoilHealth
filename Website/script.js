async function loadSoilData() {

    const status = document.getElementById("connectionStatus");

    status.innerText = "Connecting...";

    try {

        // Request data from Java
        const response = await fetch("http://localhost:8080/api/soil");

        if (!response.ok) {
            throw new Error("Could not retrieve soil data");
        }

        const data = await response.json();

        // Update average soil parameters

        document.getElementById("ph").innerText = data.average.pH;
        document.getElementById("oc").innerText = data.average.oc + "%";
        document.getElementById("ec").innerText = data.average.ec;

        document.getElementById("na").innerText = data.average.na;
        document.getElementById("k").innerText = data.average.k;
        document.getElementById("ca").innerText = data.average.ca;
        document.getElementById("mg").innerText = data.average.mg;

        // Update overall analysis

        document.getElementById("condition").innerText =
            data.analysis.soilCondition;

        document.getElementById("organic").innerText =
            data.analysis.organicStatus;

        document.getElementById("minPH").innerText =
            data.analysis.minPH;

        document.getElementById("maxPH").innerText =
            data.analysis.maxPH;

        document.getElementById("count").innerText =
            data.analysis.totalHorizons;

        // Update depth-wise table

        const table = document.getElementById("soilTable");

        table.innerHTML = "";

        data.horizons.forEach(soil => {

            const row = document.createElement("tr");

            const values = [
                soil.horizon,
                soil.depth,
                soil.pH,
                soil.oc,
                soil.ec,
                soil.na,
                soil.k,
                soil.ca,
                soil.mg,
                soil.condition
            ];

            values.forEach(value => {

                const cell = document.createElement("td");

                cell.textContent = value;

                row.appendChild(cell);

            });

            table.appendChild(row);

        });

        status.innerText = "● Connected to Java";

    } catch (error) {

        console.error(error);

        status.innerText = "● Disconnected";

    }

}

// Load data when website opens

loadSoilData();
