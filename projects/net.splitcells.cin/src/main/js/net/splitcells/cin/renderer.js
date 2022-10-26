// Three.js API doc: https://threejs.org/docs/
// Papa Parse: https://www.papaparse.com/
// Architecture: define all base variables first and than operate on these only via methods.

import * as THREE from 'three';
import { OrbitControls } from 'OrbitControls';

let worldSceneObjects = new Map(); //"const" cannot be used, as otherwise changes to the Map are not sent between the functions.
let worldVariables = new Map();
worldVariables.set('camera.position.initialized', false);

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
        const geometry = new THREE.BoxGeometry();
        const material = new THREE.MeshPhongMaterial({ color: 0x7D7D7D });
        // Wireframe is disabled for now.
        // material.wireframe = true;
        // material.wireframeLinecap = 'square';
        const cube = new THREE.Mesh(geometry, material);
        cube.position.set(1 * row[1], 1 * row[2], 0);
        scene.add(cube);
        worldSceneObjects.set(row[1] + ',' + row[2] + ',' + 0, cube);
    }
    initializeCameraPosition();
}

Papa.parse("https://raw.githubusercontent.com/www-splitcells-net/net.splitcells.cin.log/master/src/main/csv/net/splitcells/cin/log/world/main/index.csv"
    , {
        download: true
        , worker: false
        , dynamicTyping: true
        , complete: function (results) {
            addWorldData(results.data);
        }
    });

function listenToInput() {
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
}

function initializeCameraPosition() {
    if (worldVariables.get('camera.position.initialized') === false) {
        let chosenIndex = Math.floor(Math.random() * worldSceneObjects.size);
        let counter = 0;
        let chosenFocus;
        for (let key of worldSceneObjects.keys()) {
            if (counter++ >= chosenIndex) {
                chosenFocus = worldSceneObjects.get(key);
                break;
            }
        }
        controls.target.x = chosenFocus.position.x;
        controls.target.y = chosenFocus.position.y;
        controls.target.z = chosenFocus.position.z;
        worldVariables.set('camera.position.initialized', true);
    }
}

addLightToScene();
listenToInput();
animate();