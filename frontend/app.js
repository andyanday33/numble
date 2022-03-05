const app = Vue.createApp({
    //data, functions
    //template: '<h2> I am template</h2>'
    data() {
        return {
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
                body: JSON.stringify(gameMode)
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
            //TODO: implement this logic
            if (event.key == "Enter") {
                if (event.key == "Enter") {
                
                    let equationString = ""; //The string piece that is going to be sent to backend
                    let answer = 0;
                    let currentNumber = null;
                    let operation = null;
                    const num = new RegExp('[0-9]');
                    const op = new RegExp('\\+|-');
                    let valid = true;
    
                    for(i = Number(event.target.dataset.index) - 4; i <= Number(event.target.dataset.index); i++){
                        const element = this.$refs?.[`input-${i}`];
                        console.log(element);
                        equationString += String(element.value);
                        //if input is a number
                        if(num.test(element.value)) {
                            if (currentNumber == null){
                                currentNumber =  element.value;
                                console.log(currentNumber);
                            } else {
                                currentNumber += String(element.value);
                                console.log(currentNumber);
                            }
                        //if input is an operation such as + or -
                        } else if(op.test(element.value)) {
                            console.log("OPERATION")
                            if(operation == "addition"){
                                answer += Number(currentNumber);
                            } else if(operation == "extraction") {
                                answer -= Number(currentNumber);
                            } else {
                                answer = Number(currentNumber);
                            }
                            element.value == "+" ? operation = "addition" : operation = "extraction";
                            currentNumber = null;
                        }
                        //Do the operation in the last index
                        if(i == Number(event.target.dataset.index)) {
                            operation == "addition" ? answer += Number(currentNumber) : answer -= Number(currentNumber);
                        }
                       
                    }
                    console.log("answer given: " + answer);
                    console.log("answer string: " + equationString);
                    
                    if(answer == this.rightHandSide){
                        
                        
                        console.log(JSON.stringify(equationString));
                        //TODO: Implement coloring logic
                        fetch(`http://127.0.0.1:8080/game/${this.gameId}/guess`, method = {
                            method: "POST",
                            headers: {
                                'Content-Type' : 'application/json'
                            },
                            body: JSON.stringify(equationString)
                        })
                        .then(response => response.json())
                        .then(data => {
                            console.log(data);
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