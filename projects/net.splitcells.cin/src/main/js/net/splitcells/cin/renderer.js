// Three.js API doc: https://threejs.org/docs/
// Papa Parse: https://www.papaparse.com/
// Architecture: define all base variables first and than operate on these only via methods.

// Define all global variables in this section.

import * as THREE from 'three';
import { OrbitControls } from 'OrbitControls';

let worldSceneObjects = new Map();
    //x -> y -> z -> scene object
    //"const" cannot be used, as otherwise changes to the Map are not sent between the functions.
let worldVariables = new Map();
worldVariables.set('camera.position.initialized', false);
worldVariables.set('camera.focus.current', undefined);

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

// Define only functions in this section.

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

function addWorldSceneObject(x, y, z, sceneObject) {
    let xContainer = worldSceneObjects.get(x);
    if (xContainer === undefined) {
        xContainer = new Map();
        worldSceneObjects.set(x, xContainer);
    }
    let yContainer = xContainer.get(y);
    if (yContainer === undefined) {
        yContainer = new Map();
        xContainer.set(y, yContainer);
    }
    yContainer.set(z, sceneObject);
}

function getRandomWorldSceneObject() {
    let xChosenIndex = Math.floor(Math.random() * worldSceneObjects.size);
    let xCounter = 0;
    let xContainer;
    for (let key of worldSceneObjects.keys()) {
        if (xCounter++ >= xChosenIndex) {
            xContainer = worldSceneObjects.get(key);
            break;
        }
    }
    let yChosenIndex = Math.floor(Math.random() * xContainer.size);
    let yCounter = 0;
    let yContainer;
    for (let key of xContainer.keys()) {
        if (yCounter++ >= yChosenIndex) {
            yContainer = xContainer.get(key);
            break;
        }
    }
    let chosenIndex = Math.floor(Math.random() * yContainer.size);
    let counter = 0;
    for (let key of yContainer.keys()) {
        if (counter++ >= chosenIndex) {
            return yContainer.get(key);
        }
    }
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
        addWorldSceneObject(row[1], row[2], 0, cube);
    }
    initializeCameraPosition();
}

function focusNextWorldSceneObjectToLeft() {
    const x = worldVariables.get('camera.focus.current')[0];
    const y = worldVariables.get('camera.focus.current')[1];
    const z = worldVariables.get('camera.focus.current')[2];
    let nextX = undefined;
    for (let keyX of worldSceneObjects.keys()) {
        if ((keyX < nextX && keyX > x) || nextX === undefined) {
            nextX = keyX;
        }
    }
    if (nextX === undefined) {
        return;
    }
    const xContainer = worldSceneObjects.get(nextX);
    let nextY = undefined;
    for (let keyY of xContainer.keys()) {
        nextY = keyY;
        break;
    }
    for (let keyY of xContainer.keys()) {
        if (keyY < nextY && keyY > y) {
            nextX = keyX;
        }
    }
    const yContainer = xContainer.get(nextY);
    let nextZ = undefined;
    for (let keyZ of yContainer.keys()) {
        nextZ = keyZ;
        break;
    }
    for (let keyZ of yContainer.keys()) {
        if (keyZ < nextZ && keyZ > z) {
            nextZ = keyZ;
        }
    }
    const nextSceneObject = yContainer.get(nextZ);
    controls.target.x = nextSceneObject.position.x;
    controls.target.z = nextSceneObject.position.y;
    controls.target.z = nextSceneObject.position.z;
    controls.update();
}

function listenToInput() {
    function onDocumentKeyDown(event) {
        // TODO WASD is not implemented yet, because capturing D was not working.
        switch(event.code) {
            case "ArrowUp":
                controls.target.y += movement;
                break;
            case "ArrowRight":
                focusNextWorldSceneObjectToLeft();
                break;
            case "ArrowDown":
                controls.target.y -= movement;
                break;
            case "ArrowLeft":
                controls.target.x -= movement;
                break;
        }
    };
    document.addEventListener('keydown', onDocumentKeyDown);
}

function initializeCameraPosition() {
    if (worldVariables.get('camera.position.initialized') === false) {
        let chosenFocus = getRandomWorldSceneObject();
        controls.target.x = chosenFocus.position.x;
        controls.target.y = chosenFocus.position.y;
        controls.target.z = chosenFocus.position.z;
        worldVariables.set('camera.position.initialized', true);
        worldVariables.set('camera.focus.current', [chosenFocus.position.x, chosenFocus.position.y, chosenFocus.position.z]);
    }
}

// State only function calls in this section.
/* TODO This is demonstration, on how to load data from GitHub.
Papa.parse("https://raw.githubusercontent.com/www-splitcells-net/net.splitcells.cin.log/master/src/main/csv/net/splitcells/cin/log/world/main/index.csv"
    , {
        download: true
        , worker: false
        , dynamicTyping: true
        , complete: function (results) {
            addWorldData(results.data);
        }
    });*/
Papa.parse("/net/splitcells/run/conway-s-game-of-life.csv"
    , {
        download: true
        , worker: false
        , dynamicTyping: true
        , complete: function (results) {
            addWorldData(results.data);
        }
    });
addLightToScene();
listenToInput();
animate();