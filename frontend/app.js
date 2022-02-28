const app = Vue.createApp({
    //data, functions
    //template: '<h2> I am template</h2>'
    data() {
        return {
            title: 'The Final Empire',
            author: 'Brandon Sanderson',
            age: 45,
            gameStarted: false,
            hardMode: false,
            usedRows: 0,
        }
    },
    methods: {
        async startGame() {
            data = {
                'gameMode' : document.querySelector('#gameDifficultySelect').value
            }
            console.log(data.gameMode);
            if (data.gameMode == "Hard"){
                this.hardMode = true;
            } else {
                this.hardMode = false;
            }
            //this.gameStarted = true;
            fetch('http://127.0.0.1:8080/game', method = {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'
                },
                body: JSON.stringify(data)
            })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            //this.checkGameId();
        })
        .catch(err => {console.log("Error! " + err); this.gameStarted = true;});
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
                console.log("max");
              const nextElement = this.$refs?.[`input-${Number(event.target.dataset.index) + 1}`];
                if (nextElement) nextElement.focus();
            }
        },
        focusPrevOnMin(event, min) {
            if (event.target.value.length === min) {
                const prevElement = this.$refs?.[`input-${Number(event.target.dataset.index) - 1}`];
                if (prevElement) prevElement.focus();
            }
        }
    }
});

app.mount('#app')