function resetAllChanges() {
    const userConfirmed = window.confirm("Jeste li sigurni da želite resetirati sve promjene? Ova akcija će vratiti izvorno stanje baze podataka.");

    if (userConfirmed) {
        fetch("/tables/reset", { method: "POST" })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Error during reset.");
                }
                fetch("/tables")
                    .then((response) => {
                        if (!response.ok) throw new Error("Error fetching tables.");
                        return response.json();
                    })
                    .then((tables) => {
                        populateTablesDropdown(tables);
                        toggleFormsAndTable(false);
                        clearTechniquesLog();
                    })
                    .catch((error) => console.error("Error fetching tables:", error));
            })
            .catch((error) => {
                alert("Došlo je do pogreške prilikom resetiranja.");
                console.error("Error during reset:", error);
            });
    } else {
        console.log("User cancelled reset.");
    }
}

function clearTechniquesLog() {
    const logText = document.getElementById("logText");
    logText.textContent = "";
}
