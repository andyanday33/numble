const app = Vue.createApp({
    //data, functions
    //template: '<h2> I am template</h2>'
    data() {
        return {
            gameWon: false,
            gameStarted: false,
            hardMode: false,
            usedRows: 0,
            equationAnswer: null,
            gameId: null,
            rightHandSide: null,
        }
    },
    methods: {
        async startGame() {
            gameMode = document.querySelector('#gameDifficultySelect').value;
            console.log(gameMode);

            //We can implement customized number of rows and cols here in the future
            let gameReqBody = {
                    rows: 0,
                    cols: 0,
                    mode: gameMode.toUpperCase()
                };
            
            gameMode == "Hard" ? this.hardMode = true : this.hardMode = false;
            //this.gameStarted = true;

            await fetch('http://127.0.0.1:8080/game', method = {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'
                },
                body: JSON.stringify(gameReqBody)
            })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            //this.checkGameId();
            this.gameStarted = true;
            this.gameId = data;
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
            this.gameWon = false;
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
            if (event.target.value.length === max) {
                const nextElement = this.$refs?.[`input-${Number(event.target.dataset.index) + 1}`];
                console.log(nextElement);
                if (typeof nextElement !== "undefined") nextElement[0].focus();
            }
        },
        focusPrevOnMin(event, min) {
            if (event.target.value.length === min) {
                const prevElement = this.$refs?.[`input-${Number(event.target.dataset.index) - 1}`];
                if (prevElement) prevElement[0].focus();
            }
        },
        checkEquationEasy(event) {
            if (event.key == "Enter") {
        
                
                    let equationString = ""; //The string piece that is going to be sent to backend
                    let answer = 0;
                    let currentNumber = null;
                    let operation = null;
                    const num = new RegExp('[0-9]');
                    const op = new RegExp('\\+|-|\\*');
                    let valid = true;
    
                    for(i = Number(event.target.dataset.index) - 4; i <= Number(event.target.dataset.index); i++){
                        const element = this.$refs?.[`input-${i}`];
                        console.log(element);
                        if(!element){
                            valid = false;
                        } else {
                            let inputField = element[0];
                            equationString += String(inputField.value);
                            //if input is a number
                            if(num.test(inputField.value)) {
                                if (currentNumber == null){
                                    currentNumber =  inputField.value;
                                } else {
                                    currentNumber += String(inputField.value);
                                }
                            //if input is an operation such as + or -
                            } else if(op.test(inputField.value)) {
                                if(operation == "addition"){
                                    answer += Number(currentNumber);
                                } else if (operation == "extraction") {
                                    answer -= Number(currentNumber);
                                } else if (operation == "multiplication") {
                                    answer *= Number(currentNumber);
                                } else {
                                    answer = Number(currentNumber);
                                }

                                console.log(inputField.value);
                                switch (inputField.value) {
                                    case "+":
                                        operation = "addition";
                                        break;
                                    case "-":
                                        operation = "extraction";
                                        break;
                                    default:
                                        operation = "multiplication";
                                        break;
                                }
                                console.log(currentNumber);

                                currentNumber = null;
                            }
                            //Do the operation in the last index
                            if(i == Number(event.target.dataset.index)) {
                                console.log(operation);
                                switch (operation) {
                                    case "addition":
                                        answer += Number(currentNumber);
                                        break;
                                    case "extraction":
                                        answer -= Number(currentNumber);
                                        break;
                                    default:
                                        answer *= Number(currentNumber);
                                        break;
                                }
                            }
                        
                        }
                        guessReqBody = {
                            expression : equationString
                        }
                        console.log(equationString);
                        console.log(answer);
                        console.log("answer string: " + guessReqBody);
                    }
                    if(answer == this.rightHandSide && valid){
                        
                        
                        console.log(JSON.stringify(guessReqBody));
                        fetch(`http://127.0.0.1:8080/game/${this.gameId}/guess`, method = {
                            method: "POST",
                            headers: {
                                'Content-Type' : 'application/json'
                            },
                            body: JSON.stringify(guessReqBody)
                        })
                        .then(response => response.json())
                        .then(data => {

                            if(data.won){
                                console.log("WON");
                                this.gameWon = true;
                            }
                            console.log(data.won);

                            console.log(data);
                            let row = data.cells[this.usedRows];
                            console.log(row);
                            let j = 0;
                            for(let i = this.usedRows * 5; i < (this.usedRows + 1) * 5; i++) {
                                let state = row[j].state;
                                console.log(row);
                                console.log(state);
                                const col = this.$refs?.[`input-${i}`][0];
                                console.log(col);
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
                        })
                        .catch(err => {
                            alert("Equation is not right!");
                            console.error("ERROR: " + err)
                        });
                        
                    } else {
                        alert("Equation isn't right!");
                    }
                
            }
        },
        checkEquationHard() {

        }
    },
    computed: {
        //These are for UX purposes, not really necessary
        //Basically gives each input a reference string to make it possible to switch between inputs
        getReferenceEasy() {
            return (index1,index2) => `input-${index1 * 5 + index2}`
        },
        getReferenceHard() {
            return (index1, index2) => `input-${index1 * 6 + index2}`
        }
    }
});

app.mount('#app');