const app = Vue.createApp({
    //data, functions
    //template: '<h2> I am template</h2>'
    data() {
        return {
            gameEnded: false,
            gameStarted: false,
            hardMode: false,
            usedRows: 0,
            equationAnswer: null,
            gameId: null,
            rightHandSide: null,
            rows: 0,
            cols: 0
        }
    },
    methods: {
        async startGame() {
            gameMode = document.querySelector('#gameDifficultySelect').value;

            //We can implement customized number of rows and cols here in the future
            
            
            if(gameMode == "Hard") {
                this.hardMode = true;
                this.rows = 8;
                this.cols = 10;
            } else {
                this.rows = 5;
                this.cols = 5;
            }

            let gameReqBody = {
                numRows: this.rows,
                numCols: this.cols,
                mode: gameMode.toUpperCase()
            };
            //this.gameStarted = true;

            await fetch('http://127.0.0.1:8080/game', method = {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'
                },
                body: JSON.stringify(gameReqBody)
            })
        .then(response => response.json())
        .then(async data => {

            this.gameStarted = true;
            this.gameId = data;

            // await fetch(`http://127.0.0.1:8080/game/${this.gameId}`, method = {
            //     method: 'GET',
            //     headers: {
            //         'Content-Type' : 'application/json'
            //     }
            // }).then(res => res.json()).then(data => console.log(data));
        })
        .catch(err => {
            console.error("Error! " + err);
            alert("Can not connect to the server!");
        });
            //Ask for the right hand side if the game is in easy mode
            if(!this.hardMode)
            {
                fetch(`http://127.0.0.1:8080/game/${this.gameId}/rhs`, method = {
                        method: "GET",
                        headers: {
                            'Content-Type' : 'application/json'
                        },
                    })
            .then(response => response.json())
            .then(data => {
                this.rightHandSide = data;
            })
            .catch(err => {
                console.error("Error! " + err);
                alert("Could not connect to the server!");
        })
            }
        },
        async restartGame() {
            this.gameEnded = false;
            this.gameStarted = false;
            this.hardMode = false;
            this.usedRows = 0;
            this.equationAnswer = null;
            this.gameId = null;
            this.rightHandSide = null;

            await this.startGame();
        },
        checkGameId() {
            let cookie = document.cookie;
            if (!cookie.gameId) {
                this.gameStarted = true;
            } else {
                this.gameStarted = false;
            }
        },
        focusNextOnMax(event, max) {
            if(!["Shift", "Backspace", "Delete", "ArrowLeft", "ArrowRight"].includes(event.key)){
                if (event.target.value.length === max) {
                    const nextElement = this.$refs?.[`input-${Number(event.target.dataset.index) + 1}`];
                    if (typeof nextElement !== "undefined") nextElement[0].focus();
                }
            }
        },
        // focusPrevOnMin(event, min) {
        //     if(!event.shiftKey && !event.right && !event.left){
        //         if (event.target.value.length === min) {
        //             const prevElement = this.$refs?.[`input-${Number(event.target.dataset.index) - 1}`];
        //             if (typeof prevElement !== "undefined") prevElement[0].focus();
        //         }
        //     }
        // },
        focusLeft(event) {
            const prevElement = this.$refs?.[`input-${Number(event.target.dataset.index) - 1}`];
            if (typeof prevElement !== "undefined") {
                prevElement[0].focus();
                if(event.key == "Backspace" || event.key == "Delete") {
                    prevElement[0].value = "";
                }
            }
        },
        focusRight(event) {
            const nextElement = this.$refs?.[`input-${Number(event.target.dataset.index) + 1}`];
            if (typeof nextElement !== "undefined") nextElement[0].focus();
        },
        sendEquation(event) {
            if (event.key == "Enter") {
        
                
                    let equationString = ""; //The string piece that is going to be sent to backend
                    // let answer = 0;
                    // let currentNumber = null;
                    // let operation = null;
                    // const num = new RegExp('[0-9]');
                    // const op = new RegExp('\\+|-|\\*');
                    let valid = true;
    
                    for(i = this.usedRows * this.cols; i <= this.usedRows * this.cols + this.cols - 1; i++){
                        const element = this.$refs?.[`input-${i}`];
                        if(!element){
                            valid = false;
                        } else {
                            let inputField = element[0];
                            equationString += String(inputField.value);
                        }
                        guessReqBody = {
                            expression : equationString
                        }
                    }
                    if(valid){
                        fetch(`http://127.0.0.1:8080/game/${this.gameId}/guess`, method = {
                            method: "POST",
                            headers: {
                                'Content-Type' : 'application/json'
                            },
                            body: JSON.stringify(guessReqBody)
                        })
                        .then(response => response.json())
                        .then(data => {

                            if(data.error) {
                                alert(`Èrror : ${data.message} \n Tip: You must use the revealed clues inside the equation`);
                            } else {
                                if(data.won || data.lost){

                                    this.gameEnded = true;
                                }

                                let row = data.cells[this.usedRows];
    
                                let j = 0;
                                for(let i = this.usedRows * this.cols; i < (this.usedRows + 1) * this.cols; i++) {
                                    let state = row[j].state;
            
                                    const col = this.$refs?.[`input-${i}`][0];

                                    if (state == "NOT_EXIST") {
                                        col.style.background = "gray";
                                    } else if (state == "WRONG_POSITION") {
                                        col.style.background = "orange";
                                    } else {
                                        col.style.background = "green";
                                    }
                                    j++;
                                }

                                this.usedRows++;
                        }
                        }) 
                        // .catch(err => {
                            
                        //     if(this.hardMode){
                        //         alert("Equation is not right! \n Tip: If a number inside the equation is known, you must use it");
                        //     } else {
                        //         alert("The equation seems invalid \n" + err);
                        //     }
                        //     console.error("ERROR: " + err)
                        // });
                        
                    } else {
                        alert("Equation isn't right!");
                    }
                
            }
        },
    },
    computed: {
        //These are for UX purposes, not really necessary
        //Basically gives each input a reference string to make it possible to switch between inputs
        getReferenceEasy() {
            return (index1,index2) => `input-${index1 * 5 + index2}`
        },
        getReferenceHard() {
            return (index1, index2) => `input-${index1 * 10 + index2}`
        }
    }
});

app.mount('#app');