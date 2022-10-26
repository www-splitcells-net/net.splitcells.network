// Three.js API doc: https://threejs.org/docs/
// Architecture: define all base variables first and than operate on these only via methods.

import * as THREE from 'three';
import { OrbitControls } from 'OrbitControls';

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
camera.position.z = 5;
const movement = 1;

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
renderer.domElement.style.height = "50%";
renderer.domElement.style.width = "100%";
document.getElementById("net-splitcells-canvas-main").appendChild(renderer.domElement);

const controls = new OrbitControls(camera, renderer.domElement);
controls.enableDamping = true;
controls.enablePan = false;

function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
    controls.update();
};

function addLightToScene() {
    const mainLightColor = 0xFFFFFF;
    const mainLightIntensity = 1;
    const mainLight = new THREE.PointLight(mainLightColor, mainLightIntensity);
    mainLight.position.set(0, 1000, 1000);
    scene.add(mainLight);

    const ambientLightColor = 0xFFFFFF;
    const ambientLightIntensity = .5;
    const ambientLight = new THREE.AmbientLight(ambientLightColor, ambientLightIntensity);
    scene.add(ambientLight);
}

function addWorldData(updatedData) {
    var rowIndex;
    for (rowIndex = 1; rowIndex < updatedData.length; rowIndex++) {
        var row = updatedData[rowIndex];
        console.log(row);
        const geometry = new THREE.BoxGeometry();
        const material = new THREE.MeshPhongMaterial({ color: 0x7D7D7D });
        // Wireframe is disabled for now.
        // material.wireframe = true;
        // material.wireframeLinecap = 'square';
        const cube = new THREE.Mesh(geometry, material);
        cube.position.set(1 * row[1], 1 * row[2], 0);
        scene.add(cube);
    }
}

Papa.parse("https://raw.githubusercontent.com/www-splitcells-net/net.splitcells.cin.log/master/src/main/csv/net/splitcells/cin/log/world/main/index.csv"
    , {
        download: true
        , dynamicTyping: true
        , complete: function (results) {
            addWorldData(results.data);
        }
    });

function onDocumentKeyDown(event) {
    var keyCode = event.which;
    if (keyCode == 87) { // W
        controls.target.y += movement;
    } else if (keyCode == 83) { // S
        controls.target.y -= movement;
    } else if (keyCode == 65) { // A
        controls.target.x -= movement;
    } else if (keyCode == 68) { // D
        controls.target.x += movement;
    }
    controls.update();
};
document.addEventListener("keydown", onDocumentKeyDown, false);

addLightToScene()

animate();