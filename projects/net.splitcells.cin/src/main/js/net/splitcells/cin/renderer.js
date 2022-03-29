            import * as THREE from 'three';
			import { OrbitControls } from 'OrbitControls';

            const scene = new THREE.Scene();
			const camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
			const movement = 1;

			const renderer = new THREE.WebGLRenderer();
			renderer.setSize(window.innerWidth, window.innerHeight);
			renderer.domElement.style.height = "50%";
			renderer.domElement.style.width = "100%";
			document.getElementById("net-splitcells-canvas-main").appendChild(renderer.domElement);

			camera.position.z = 5;

            const controls = new OrbitControls(camera, renderer.domElement);

			function animate() {
				requestAnimationFrame(animate);
				renderer.render(scene, camera);
			};

			function updateWorldData(updatedData) {
                var rowIndex = 1
                for (rowIndex = 1; rowIndex < updatedData.length; rowIndex++) {
                        var row = updatedData[rowIndex];
                        console.log(row);
                        const geometry = new THREE.BoxGeometry();
			            const material = new THREE.MeshBasicMaterial({color: 0xffffff});
			            material.wireframe = true;
			            material.wireframeLinecap  = 'square';
			            const cube = new THREE.Mesh(geometry, material);
			            cube.position.set(1 * row[1], 1 * row[2], 0);
			            scene.add(cube);
                    }
                }
            Papa.parse("https://raw.githubusercontent.com/www-splitcells-net/net.splitcells.cin.log/master/src/main/csv/net/splitcells/cin/log/world/main/index.csv"
                    , {download: true
                        ,dynamicTyping: true
                        ,complete: function(results) {
                            updateWorldData(results.data);
                            }});

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

			animate();