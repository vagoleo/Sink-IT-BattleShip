

const templates = {
    login: `<div id="logindiv">
                 <img id="logo" src="assets/SINKITPNGLOGO.png" alt="">

           <h1>Login</h1>
            <form @submit.prevent="submitinfo('login', 'loginform')" id="loginform" method="post" class="ui inverted form">
              <div class="field">
                  <label>Username:</label>
                  <div class="ui small left icon input focus">
                      <input type="text" name="username" autocomplete="username">
                      <i class="user icon"></i>
                  </div>
              </div>
              <div class="field">
                  <label>Password:</label>
                  <div class="ui small left icon input focus">
                      <input type="password" name="password" autocomplete="current-password">
                      <i class="lock icon"></i>
                  </div>
              </div>
                </br>
              <div>
                  <button id="loginbtn" class="ui red button" type="submit">Log in</button>
              </div>
              <div class="ui horizontal divider"><p style="color: rgba(255,255,255,.9)">Or</p></div>
            </form>
            <button class="ui grey button" @click="$emit('viewx', view)">Register</button>
        </div>`,
    register: `<div id="regdiv">
            <h1>Register</h1>
            <form class="ui inverted form" @submit.prevent="submitinfo('players', 'regform')" id="regform" method="post">
              
               <div class="field">
                   <label>Username:</label>
                   <input class="ui input focus" type="text" name="username" placeholder="Example69" autocomplete="username">
               </div>
               <div class="field">
                   <label>Name:</label>
                   <div class="two fields">
                      <div class="field">
                          <input class="ui input focus" type="text" name="firstName" placeholder="First Name">
                      </div>
                      <div class="field">
                          <input class="ui input focus" type="text" name="lastName" placeholder="Last Name">
                      </div> 
                   </div>
               </div>
               <div class="field">
                   <label>Password:</label>
                   <input class="ui input focus" type="password" name="password" placeholder="●●●●●●●" autocomplete="new-password">
               </div>
            </br>
               <div>
                   <button class="ui red button" type="submit">Submit</button>
               </div>
               <div style="display: flex; justify-content:center;">
                    </br>
                   <p>Already have an account? <a class="hover" @click="$emit('viewx', view)">Log in.</a></p>
               </div>
            </form>
        </div>`
}



//------------[VUE]-------------//
var app = new Vue ({
    el: "#app",
    data: {
        view: 'login',
        games: [],
        currentPlayer: 'asd'
        
    },
    created() {
        this.getData()
    },
    methods: {
        joinGame(gameid){
            fetch(`/api/games/${gameid}/players`, {
                method: 'POST'
            })
            .then(res =>  {
                if(res.ok){
                    return res.json()
                }else {
                    throw new Error()
                }
            })
            .then(json => {
                alert(json.message)
                location.href = '/web/game.html?gp=' + json.gpid
            })
            .catch (function(error){
              console.log(error)
          })
        },
        
        createGame(){
          fetch ('/api/games', {
              method: 'POST'
          })
            .then(response => {
              if(response.ok){
                  return response.json()
              }else{
                  throw new Error()
              }
          })
            .then (json =>{
              location.href = '/web/game.html?gp=' + json.gpid
          })
            .catch (function(error){
              console.log(error)
          })
        },
        
        findScore(score, playerid){
            let aux = 0;
            for(let i=0; i<this.games.length; i++){
                for(let j=0; j<this.games[i].gamePlayers.length; j++){
                    if(this.games[i].gamePlayers[j].player.playerid == playerid && this.games[i].gamePlayers[j].score == score){
                        aux++
                    }
                }
            }
            return aux;
        },
        
        getScores(caso, playerid){  
            let aux=0;
            switch (caso){
            case "wins":
                aux = this.findScore(10, playerid);
                break;
            case "loses":
                aux = this.findScore(0, playerid);
                break;
            case "ties":
                aux = this.findScore(5, playerid);
                break;
            case "total":
                for(let i=0; i<this.games.length; i++){
                    for(let j=0; j<this.games[i].gamePlayers.length; j++){
                        if(this.games[i].gamePlayers[j].player.playerid == playerid){
                            aux += this.games[i].gamePlayers[j].score
                        }
                    }
                }
            }
            return aux;
        },
        
        removeDuplicates(array, key) {
            let lookup = {};
            let result = [];
            array.forEach(element => {
                if(!lookup[element[key]]) {
                    lookup[element[key]] = true;
                    result.push(element);
                }
            });
            return result;
        },
        
        getData (){
            
            fetch (`/api/games`, {
                method: 'GET',
            })
            .then (res =>{
                if (res.ok){
                    return res.json()
                }else {throw new Error()}
            })
            .then (result =>{
                app.games = result.games
                app.currentPlayer = result.currentPlayer
            })
            .catch (function(error){
                console.log(error)
            })
            
        },
        
        submitinfo(log, id){

            fetch (`/api/`+log, {
                method: 'POST',
                body: new FormData(document.getElementById(id))
            })
            .then(response =>{
                if(response.ok) {
                    switch (log){
                        case 'login':
                            app.getData();
                            app.playaudio();
                            location.href = '/web/games.html'
                            break;
                        case 'logout':
                            app.getData();
                            app.pauseaudio();
                            break;
                        case 'players':
                            app.submitinfo('login', 'regform')
                            break;
                    }
                    return response.json()
                } else {
                    return response.json()
                }
            })
            .then(result =>{
                alert(result.message)
            })
            .catch(function(error){
            })
            
            document.querySelectorAll('.emptyinput').forEach(i => i.value="");

        },
        
        showmodal(){
            asd();
        },
      
      playaudio(){
        document.querySelector('#audio').play()
      },
      
      pauseaudio(){
        document.querySelector('#audio').pause()
      }
        
        
    }, /*Methods*/
    computed: {
        playersList(){
            let aux =[];
            for(let i=0; i<this.games.length; i++){
                for(let j=0; j<this.games[i].gamePlayers.length; j++){
                   aux.push(this.games[i].gamePlayers[j].player)
                }
            }
            
            aux.forEach(player => {
              player.total = this.getScores("total", player.playerid)
              player.wins = this.getScores("wins", player.playerid)
              player.loses = this.getScores("loses", player.playerid)
              player.ties = this.getScores("ties", player.playerid)
            })
            
            return this.removeDuplicates(aux, "playerid").sort((a, b) => (a.total < b.total) ? 1 : -1)
        }
    },
    components: {
        login: {
            props: ['submitinfo'],
            data: function (){
                return {
                    view: 'register',  
                }
            },
            template: templates.login,
        },
        register: {
            props: ['submitinfo'],
            data: function (){
                return {
                    view: 'login',  
                }
            },
            template: templates.register
        }
    }
})
//------------[VUE]-------------//

$('.ui.dropdown')
  .dropdown()
;

function asd(){
    $('.ui.modal')
  .modal('show')
;
}
