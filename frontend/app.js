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
            if (gameMode == "Hard"){
                this.hardMode = true;
            } else {
                this.hardMode = false;
            }
            //this.gameStarted = true;
            await fetch('http://127.0.0.1:8080/game', method = {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'
                },
                body: JSON.stringify(gameMode.toUpperCase())
            })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            //this.checkGameId();
            this.gameStarted = true;
            this.gameId = data;
        })
        .catch(err => {console.error("Error! " + err);});
            fetch(`http://127.0.0.1:8080/game/${this.gameId}/rhs`, method = {
                method: "GET",
                headers: {
                    'Content-Type' : 'application/json'
                },
            })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            this.rightHandSide = data;
        })
        .catch(err => console.error("Error! " + err))
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
                if (nextElement) nextElement.focus();
            }
        },
        focusPrevOnMin(event, min) {
            if (event.target.value.length === min) {
                const prevElement = this.$refs?.[`input-${Number(event.target.dataset.index) - 1}`];
                if (prevElement) prevElement.focus();
            }
        },
        checkEquation(event) {
            //TODO: Implement multiplication!
            if (event.key == "Enter") {
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
                        equationString += String(element.value);
                        //if input is a number
                        if(num.test(element.value)) {
                            if (currentNumber == null){
                                currentNumber =  element.value;
                            } else {
                                currentNumber += String(element.value);
                            }
                        //if input is an operation such as + or -
                        } else if(op.test(element.value)) {
                            if(operation == "addition"){
                                answer += Number(currentNumber);
                            } else if (operation == "extraction") {
                                answer -= Number(currentNumber);
                            } else if (operation == "multiplication") {
                                answer *= Number(currentNumber);
                            } else {
                                answer = Number(currentNumber);
                            }

                            console.log(element.value);
                            switch (element.value) {
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
                        guess : equationString
                    }
                    console.log(equationString);
                    console.log(answer);
                    console.log("answer string: " + guessReqBody);
                    
                    if(answer == this.rightHandSide){
                        
                        
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
                                const col = this.$refs?.[`input-${i}`];
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
                        .catch(err => console.error("ERROR: " + err));
                        
                    } else {
                        alert("Equation isn't right!");
                    }
                
            
                }
            }
        }
    }
});

app.mount('#app');