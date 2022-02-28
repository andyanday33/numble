const app = Vue.createApp({
    //data, functions
    //template: '<h2> I am template</h2>'
    data() {
        return {
            title: 'The Final Empire',
            author: 'Brandon Sanderson',
            age: 45,
            gameStarted: false,
        }
    },
    methods: {
        async startGame() {
            data = {
                'gameMode' : document.querySelector('#gameDifficultySelect').value
            }
            console.log(data.gameMode);
            fetch('https://127.0.0.1:8080/game', method = {
                method: 'POST',
                headers: {
                    'Content-Type' : 'application/json'
                },
                body: JSON.stringify(data)
            })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            this.checkGameId();
        })
        .catch(err => console.log("Error! " + err));
        },
        checkGameId() {
            let cookie = document.cookie;
            if(!cookie.gameId){
                gameStarted = true;
            } else {
                gameStarted = false;
            }
        }
    }
});

app.mount('#app')