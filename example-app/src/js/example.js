import { CapacitorStorefront } from 'capacitor-storefront';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    CapacitorStorefront.echo({ value: inputValue })
}
