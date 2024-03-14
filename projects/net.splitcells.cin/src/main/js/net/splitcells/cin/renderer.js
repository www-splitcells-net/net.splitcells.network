/* SPDX-License-Identifier: EPL-2.0 OR GPL-2.0-or-later
 * SPDX-FileCopyrightText: Contributors To The `net.splitcells.*` Projects
 */
/* Three.js API doc: https://threejs.org/docs/
 * Papa Parse: https://www.papaparse.com/
 * Architecture: define all base variables first and than operate on these only via methods.
 */
/* A bundler like rollup was not used, as the developer could not get it running with rollup.
 * The Three.js project and most Three.js developers seem to prefer to just link to the Three.js bundle and
 * its addons.
 * Therefore, it was decided to not further pursuit bundlers for now.
 */

import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

// Define all global variables in this section.

var worldSceneObjects = [];
    // Contains the list of all interactable scene objects.
    //"const" cannot be used, as otherwise changes to the Map are not sent between the functions.
var worldVariables = {
    camera_focus_current: undefined,
    debug_enabled: undefined,
    // When world data is imported, only data of this time is processed. This shows the world at that specific time.
    render_specific_time: undefined,
    /* When world data is imported, only data of the latest time is processed.
     * Most of the time, this shows the currently predicted world's state for the furthest point in the future.
     * Some times, this will show the world's state at the current time.
     */
    render_latest_time: true,
    world_import_from: undefined,
    load_time_last: undefined,
    load_data_function:  undefined
};
const worldSceneObjectDefaultColor = 0x7D7D7D;
const worldSceneObjectHighlightedColor = 0x8D8D8D;

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
camera.position.z = 5;

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.getElementById("topElement").appendChild(renderer.domElement);

const controls = new OrbitControls(camera, renderer.domElement);
controls.enableDamping = true;
controls.enablePan = false;

if (window != undefined) {
    const browserUrl = window.location.search;
    const urlParams = new URLSearchParams(browserUrl);
    const world_import_from = urlParams.get('world_import_from');
    worldVariables.world_import_from = world_import_from;
    worldVariables.debug_enabled = urlParams.get('debug_enabled');
    worldVariables.render_specific_time = urlParams.get('render_specific_time');
    worldVariables.render_latest_time = urlParams.get('render_latest_time');
}

if (worldVariables.debug_enabled == 1) {
    scene.add(new THREE.AxesHelper(5)); // https://threejs.org/docs/#api/en/helpers/AxesHelper
}

var gamepad = {
    device_state: undefined,
    device_id: undefined,
    update_time_last: Date.now(),
    update_time_interval: 100,
    button_map: {
        'D-Pad-Top': 12,
        'D-Pad-Right': 15,
        'D-Pad-Bottom': 13,
        'D-Pad-Left': 14
    },
    axe_map: {
        'D-Pad-Horizontal': 6,
        'D-Pad-Vertical': 7
    },
	connect: function(evt) {
		gamepad.device_state = evt.gamepad;
        gamepad.device_id = evt.gamepad.index;
        gamepad.update_time_last = performance.now();
        console.log('Gamepad device connected.');
	},
	disconnect: function(evt) {
		gamepad.device_state = undefined;
        gamepad.device_id = undefined;
        gamepad.update_time_last = performance.now();
		console.log('Gamepad device disconnected.');
	},
    isButtonPressed: function(button) {
        if (gamepad.device_state.buttons[button] == undefined) {
            return false;
        }
		return gamepad.device_state.buttons[button].pressed;
	},
    getAxeValue: function(axe) {
        if (gamepad.device_state.axes[axe] == undefined) {
            return undefined;
        }
		return gamepad.device_state.axes[axe];
	},
    isDeviceReady: function(currentTime) {
        if (gamepad.device_state == undefined) {
            return false;
        }
        let delta = currentTime - gamepad.update_time_last;
        if (delta < gamepad.update_time_interval) {
            return false;
        }
		return gamepad.device_state != undefined;
	},
    scanState: function(currentTime) {
        if (gamepad.device_id == undefined) {
            return;
        }
        gamepad.update_time_last = currentTime;
        gamepad.device_state = navigator.getGamepads()[gamepad.device_id];
    }
};

// Define only functions in this section.

function render_loop(currentTime) {
    if (worldVariables.load_time_last === undefined) {
        worldVariables.load_time_last = currentTime;
    }
    let numberOfSecondsSince = (currentTime - worldVariables.load_time_last);
    if (numberOfSecondsSince > 1000) {
        worldVariables.load_time_last = currentTime;
        load_loop();
    }
    update_by_gamepad(currentTime);
    renderer.render(scene, camera);
    controls.update();
    requestAnimationFrame(render_loop);
}

function update_by_gamepad(currentTime) {
    if (gamepad.isDeviceReady(currentTime)) {
        gamepad.scanState(currentTime);
        // TODO console.log(gamepad.device_state.timestamp);
        /* For reading directional buttons the buttons and the axes have to checked,
         * because on Firefox only the axes values are set.
         */
        if (gamepad.isButtonPressed(12)) {
            camera_focus_worldSceneObject_to_forward();
        } else if (gamepad.isButtonPressed(15)) {
            camera_focus_worldSceneObject_to_right();
        } else if (gamepad.isButtonPressed(13)) {
            camera_focus_worldSceneObject_to_backward();
        } else if (gamepad.isButtonPressed(14)) {
            camera_focus_worldSceneObject_to_left();
        } else if (gamepad.getAxeValue(7) == -1) {
            camera_focus_worldSceneObject_to_forward();
        } else if (gamepad.getAxeValue(6) == 1) {
            camera_focus_worldSceneObject_to_right();
        } else if (gamepad.getAxeValue(7) == 1) {
            camera_focus_worldSceneObject_to_backward();
        } else if (gamepad.getAxeValue(6) == -1) {
            camera_focus_worldSceneObject_to_left();
        }
    }
}

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

function worldScenesObject_add(sceneObject) {
    scene.add(sceneObject);
    worldSceneObjects.push(sceneObject);
}

function worldScenesObject_remove_by_index(i) {
    scene.remove(worldSceneObjects[i]);
    worldSceneObjects.splice(i, 1);
}

function worldScenesObject_clear() {
    let size = worldSceneObjects.length;
    for (var i = 0; i < size; i++) {
        if (worldSceneObjects[i] != undefined) {
            worldScenesObject_remove_by_index(i);
        }
    }
}

function worldSceneObject_get_random() {
    let chosenIndex = Math.floor(Math.random() * worldSceneObjects.length);
    return worldSceneObjects[chosenIndex];
}

function worldSceneObjects_import(updatedData) {
    let rowIndex;
    let latestTime = undefined;
    if (worldVariables.render_latest_time == 1) {
        latestTime = 0;
        for (rowIndex = 1; rowIndex < updatedData.length; rowIndex++) {
            let rowTime = updatedData[rowIndex][0];
            if (rowTime > latestTime) {
                latestTime = rowTime;
            }
        }
        console.log('Rendering latest time of '.concat(latestTime, '.'));
    }
    for (rowIndex = 1; rowIndex < updatedData.length; rowIndex++) {
        let row = updatedData[rowIndex];
        if (worldVariables.render_specific_time != undefined) {
            if (row[0] != worldVariables.render_specific_time) {
                continue;
            }
        }
        if (worldVariables.render_latest_time == 1) {
            let rowTime = row[0];
            if (rowTime != latestTime) {
                continue;
            }
        }
        if (row[3] != 0) {
            const geometry = new THREE.BoxGeometry();
            const material = new THREE.MeshPhongMaterial({ color: worldSceneObjectDefaultColor });
            // Wireframe is disabled for now.
            // material.wireframe = true;
            // material.wireframeLinecap = 'square';
            const cube = new THREE.Mesh(geometry, material);
            cube.position.set(1 * row[1], 0, 1 * row[2]);
            worldScenesObject_add(cube);
        }
    }
}

function camera_focus_worldSceneObject_to_forward() {
    camera_focus_worldSceneObject_nearest_on_condition((nextObject, camera_focus_current) => nextObject.position.z < camera_focus_current.position.z);
}

function camera_focus_worldSceneObject_to_right() {
    camera_focus_worldSceneObject_nearest_on_condition((nextObject, camera_focus_current) => nextObject.position.x > camera_focus_current.position.x);
}

function camera_focus_worldSceneObject_to_backward() {
    camera_focus_worldSceneObject_nearest_on_condition((nextObject, camera_focus_current) => nextObject.position.z > camera_focus_current.position.z);
}

function camera_focus_worldSceneObject_to_left() {
    camera_focus_worldSceneObject_nearest_on_condition((nextObject, camera_focus_current) => nextObject.position.x < camera_focus_current.position.x);
}

function camera_focus_worldSceneObject_nearest() {
    camera_focus_worldSceneObject_nearest_on_condition((nextObject, camera_focus_current) => true);
    }

function camera_focus_worldSceneObject_nearest_on_condition(condition) {
    let camera_focus_current = worldVariables.camera_focus_current;
    if (camera_focus_current != undefined) {
        const currentX = camera_focus_current.position.x;
        const currentY = camera_focus_current.position.y;
        const currentZ = camera_focus_current.position.z;
        let nearestRight = undefined;
        let nearestDistance = undefined;
        for (const nextObject of worldSceneObjects) {
            let isConditionMet = condition(nextObject, camera_focus_current);
            if (nearestRight == undefined && isConditionMet) {
                nearestRight = nextObject;
                nearestDistance = Math.sqrt(Math.pow(currentX - nextObject.position.x, 2)
                    + Math.pow(currentY - nextObject.position.y, 2)
                    + Math.pow(currentZ - nextObject.position.z, 2));
            } else {
                if (isConditionMet) {
                    let nextDistance = Math.sqrt(Math.pow(currentX - nextObject.position.x, 2)
                                    + Math.pow(currentY - nextObject.position.y, 2)
                                    + Math.pow(currentZ - nextObject.position.z, 2));
                    if (nextDistance < nearestDistance) {
                        nearestRight = nextObject;
                        nearestDistance = nextDistance;
                    }
                }
            }
        }
        if (nearestRight != undefined) {
            camera_focus_on_sceneObject(nearestRight);
        }
    }
}

function camera_focus_on_sceneObject(sceneObject) {
    let camera_focus_current = worldVariables.camera_focus_current;
    if (camera_focus_current != undefined) {
        camera_focus_current.material.color.setHex(worldSceneObjectDefaultColor);
    }
    controls.target.x = sceneObject.position.x;
    controls.target.z = sceneObject.position.y;
    controls.target.z = sceneObject.position.z;
    sceneObject.material.color.setHex(worldSceneObjectHighlightedColor);
    worldVariables.camera_focus_current = sceneObject;
    controls.update();
}

function listenToInput() {
    function onDocumentKeyDown(event) {
        // TODO WASD is not implemented yet, because capturing D was not working.
        switch(event.code) {
            case "ArrowUp":
                camera_focus_worldSceneObject_to_forward();
                break;
            case "ArrowRight":
                camera_focus_worldSceneObject_to_right();
                break;
            case "ArrowDown":
                camera_focus_worldSceneObject_to_backward();
                break;
            case "ArrowLeft":
                camera_focus_worldSceneObject_to_left();
                break;
        }
    };
    document.addEventListener('keydown', onDocumentKeyDown);
}

function camera_position_initialize() {
    let chosenFocus = worldSceneObject_get_random();
    camera_focus_on_sceneObject(chosenFocus);
    worldVariables.camera_focus_current = chosenFocus;
}

function load_init() {
    worldVariables.last_load_function((results) => {
        worldSceneObjects_import(results.data);
        addLightToScene();
        listenToInput();
        camera_position_initialize();
    });
}

function load_loop() {
    worldVariables.last_load_function((results) => {
        worldScenesObject_clear();
        worldSceneObjects_import(results.data);
        camera_focus_worldSceneObject_nearest();
    });
}

function sleep_for_ms(milliseconds) {
    return new Promise(resolve => setTimeout(resolve, milliseconds));
}

// State only function calls in this section.
window.addEventListener("gamepadconnected", gamepad.connect);
window.addEventListener("gamepaddisconnected", gamepad.disconnect);
if (worldVariables.world_import_from == undefined) {
    worldVariables.last_load_function = (completeFunction) => {
            Papa.parse("https://raw.githubusercontent.com/www-splitcells-net/net.splitcells.cin.log/master/src/main/csv/net/splitcells/cin/log/world/main/index.csv"
                , {
                    download: true
                    , worker: false
                    , dynamicTyping: true
                    , complete: completeFunction});
        }
} else if (worldVariables.world_import_from === 'live') {
    worldVariables.last_load_function = (completeFunction) => {
        Papa.parse("/net/splitcells/run/world-history/allocations/world-history/world-history.csv"
            , {
                download: true
                , worker: false
                , dynamicTyping: true
                , complete: completeFunction});
    }
}
load_init();
render_loop();