

let params = new URLSearchParams(location.search)
let gp = params.get('gp')


//------------- [VUE] ---------------

var app = new Vue ({
    el: "#app",
    data: {
        gvdata: {},
        gp: '',
        shotCount: 0,
        gameState:''
    },
    created() {
        this.getData(gp);
        setInterval(function(){
              app.getData(gp)
            }, 5000);
    },
  
    watch: {
        gameState: function (){
            this.checkGameState()
        }
    },
    methods: {
        getData (gpId){
            fetch (`/api/game_view/${gpId}`, {
                method: 'GET',
            })
            .then (function(response){
                if (response.ok){
                    return response.json()
                }else {throw new Error()}
            })
            .then (function(result){
                app.gvdata = result;
                app.gp = gp;
                app.gameState = result.gameState;
                app.showMySalvoes();
                app.showEnemyHits();

            })
            .catch (function(error){
                console.log(error)
            })
        },
        
        displayShips(){
            let busycells = document.querySelectorAll('.busycell')
            if(this.gvdata.ships.length == 5 && busycells.length == 17) {
                this.gvdata.ships.forEach(ship => {
                    createShips(ship.type, ship.locations.length, this.positionfn(ship.locations), document.getElementById('ships' + ship.locations[0]), true)
                })  
            }else if (busycells.length == 0){
              createShips('carrier', 5, 'horizontal', document.getElementById('dock'),false)
              createShips('battleship', 4, 'horizontal', document.getElementById('dock'),false)
              createShips('destroyer', 3, 'horizontal', document.getElementById('dock'),false)
              createShips('submarine', 3, 'horizontal', document.getElementById('dock'),false)
              createShips('patrol_boat', 2, 'horizontal', document.getElementById('dock'),false)
              
            }
        },
        
        showMySalvoes(){
            
            let mySalvoes = this.gvdata.salvoes.filter(x => x.playerid == this.gvdata.playerview_id)
            
            for(let i=0; i<mySalvoes.length; i++){
                mySalvoes[i].hits.forEach(hit => {
                    let aux = document.getElementById('salvo'+hit)
                    aux.classList.add('firedSalvoHitted')
                    aux.innerHTML = mySalvoes[i].turn
                })
                
                mySalvoes[i].locations.forEach(loc => {
                    let auxi = document.getElementById('salvo'+loc)
                    if(!auxi.classList.contains('firedSalvoHitted')){
                        auxi.classList.add('firedSalvoMissed')
                        auxi.innerHTML = mySalvoes[i].turn
                    }
                })
                
                mySalvoes[i].sunkenShips.forEach(ss => {
                    ss.locations.forEach(loc => {
                        let auxe = document.getElementById('salvo'+loc)
                        auxe.classList.add('sunken')
                    })
                })
            }
            
        },
        
        showEnemyHits(){
            //Arr with my ships locations
            let myShips = []
            for (let i=0; i<this.gvdata.ships.length; i++){
                let shipi = this.gvdata.ships[i]
                for (let j=0; j<shipi.locations.length; j++){
                    myShips.push(shipi.locations[j])
                }
            }
            
            //Arr with enemy salvos locations
            let enemySalvos = []
            for (let i=0; i<this.gvdata.salvoes.length; i++){
                let salvo = this.gvdata.salvoes[i]
                if (salvo.playerid != this.gvdata.playerview_id){
                    for (let j=0; j<salvo.locations.length; j++){
                        enemySalvos.push(salvo.locations[j])
                    }
                }
            }
            
            //Add 'busycell' class to locations where I have my ships
            for (let i=0; i<myShips.length; i++){
                document.getElementById('ships'+myShips[i]).classList.add('busycell')
            }
            
            //Add classes Hitted or Missed based on the enemy shot
            for (let i=0; i<enemySalvos.length; i++){
                let aux = document.getElementById('ships'+enemySalvos[i])
                if (aux.classList.contains('busycell')){
                    aux.classList.add('hitted')
                }else{
                    aux.classList.add('missed')
                }
            }
        },
        
        positionfn(arr){
            let aux = (arr[0][0] == arr[1][0])? 'horizontal' : 'vertical'
            return aux;
        },
        
        
        aim(event){
            if(this.shotCount < 2 && (event.target.classList.contains('aimable')) && (!event.target.classList.contains('firedSalvo'))){
                event.target.classList.toggle('aimed')
            } else if (this.shotCount == 2) {
                event.target.classList.remove('aimed')
            }
            this.shotCount = document.querySelectorAll('.aimed').length
        },
        
        addShips(){
            let ships = []
            document.querySelectorAll(".grid-item").forEach( e => { 
                let ship = {}
                ship.type = e.id
                ship.locations = []

                if(e.dataset.orientation == "horizontal"){
                    for(i = 0; i < e.dataset.length; i++){
                        ship.locations.push(e.dataset.y + (parseInt(e.dataset.x) + i))
                    }
                }else{
                    for(i = 0; i < e.dataset.length; i++){
                        ship.locations.push(String.fromCharCode(e.dataset.y.charCodeAt() + i) + e.dataset.x)
                    }
                }
                ships.push(ship)
            })
            this.sendShips(ships,gp)
        },
        
        sendShips (ships, gamePlayerId){
            fetch (`/api/games/players/${gamePlayerId}/ships`, {
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(ships)
            })
            .then (response => {
                if(response.ok){
                    this.getData(this.gp)
                    location.href = '/web/game.html?gp=' + this.gp;
                    return response.json()
                    
                }else {return response.json()}
            })
            .then (json => {
                alert(json.message)
            })
            .catch (function(error){
                alert(error.message)
            })
        },
        
        sendSalvos (gamePlayerId){
            let shots = [];
            let aux = document.querySelectorAll('.aimed')
            let aux2 = Array.from(aux)
            aux2.filter(x => (!x.classList.contains('firedSalvo'))).forEach(s => {
                let loc = s.dataset.y + s.dataset.x
                shots.push(loc)
            })
            
            fetch(`/api/games/players/${gamePlayerId}/salvoes`,{
                method: 'POST',
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(shots)
            })
            .then (response => {
                if(response.ok){
                    this.getData(this.gp)
                    this.shotCount = 0;
                    location.href = '/web/game.html?gp=' + this.gp;
                    return response.json()
                } else {return response.json()}
            })
            .then (json => {
                console.log(json.message)
            })
            .catch (function(error){
                console.log(error)
            })
        },
      
      goback(){
        location.href='/web/games.html'
      },
      
      checkGameState(){
        let allgrids = document.querySelector('#allgrids')
        let display = document.querySelector('#display')
        let displayp = document.querySelector('#display p')
        let shipsbtn = document.querySelector('#shipsbtn')
        let shootbtn = document.querySelector('#shootbtn')
        let grid = document.querySelector('#grid')
        let salvogrid = document.querySelector('#grid-salvo')
        let dock = document.querySelector('#dock')
        
        switch(this.gameState){
          case 'WAIT_OPPONENT':
            allgrids.style.display='none'
            dock.style.display='none'
            salvogrid.style.display='none'
            grid.style.display='none'
            display.style.display='none'
            shipsbtn.style.display='none'
            shootbtn.style.display='none'
            break;
            
          case 'ENTER_SHIPS':
            allgrids.style.display='flex'
            dock.style.display='flex'
            shootbtn.style.display='none'
            salvogrid.style.display='none'
            grid.style.display='none'
            display.style.display='none'
            shipsbtn.style.display='none'
            
            setTimeout(function(){ 
                document.querySelector('#p1').classList.add('animated','slideOutLeft')
                document.querySelector('#p2').classList.add('animated','slideOutRight')
                document.querySelector('#vsscreen').classList.add('animated','fadeOut')
                display.style.display='flex'
                displayp.innerText='Place your ships!'
            }, 2500);
            setTimeout(function(){ 
                displayp.classList.add('animated','slideOutRight')
                display.classList.add('animated','fadeOut')
            }, 5000);
            setTimeout(function(){
                grid.style.display='block'
                app.displayShips();
                grid.style.border='1.4px solid yellow'
                grid.classList.add('animated', 'pulse')
                shipsbtn.style.display='block'
                display.style.display='none'
                document.querySelector('#vsscreen').style.display='none'
            }, 5500);
            
            break;
            
          case 'WAIT_OPPONENT_SHIPS':
            dock.style.display='none'
            salvogrid.style.display='none'
            grid.style.display='none'
            display.style.display='none'
            shipsbtn.style.display='none'
            shootbtn.style.display='none'

            break;
          
          case 'WAIT':
            dock.style.display='none'
            salvogrid.style.display='block'
            grid.style.display='block'
            app.displayShips();
            display.style.display='flex'
            displayp.innerText="It's your opponent's turn to shoot"
            displayp.classList.add('animated','slideInLeft')
            displayp.classList.remove('slideOutRight')
            display.classList.remove('fadeOut')
            shipsbtn.style.display='none'
            shootbtn.style.display='none'
            setTimeout(function(){ 
               displayp.classList.add('animated','slideOutRight')
               display.classList.add('animated','fadeOut')
               
            }, 2500);
            setTimeout(function(){ 
               grid.style.border='1.4px solid yellow'
               salvogrid.style.border='1px solid grey'
               grid.classList.add('animated', 'pulse')
               display.style.display='none'
            }, 2700);
    
            break;
            
          case 'FIRE':
            dock.style.display='none'
            grid.style.display='block'
            app.displayShips();
            salvogrid.style.display='block'
            display.style.display='flex'
            display.classList.remove('fadeOut')
            displayp.innerText="It's your turn to shoot!"
            shipsbtn.style.display='none'
            shootbtn.style.display='block'
            displayp.classList.remove('slideOutRight')
            displayp.classList.add('animated','slideInLeft')
        
            setTimeout(function(){ 
               displayp.classList.add('animated','slideOutRight')
               display.classList.add('animated','fadeOut')
            }, 2500);
            setTimeout(function(){ 
               salvogrid.style.border='1.4px solid yellow'
               grid.style.border='1px solid grey'
               salvogrid.classList.add('animated', 'pulse')
               display.style.display='none'
            }, 2700);

            break;
            
          case 'WON':
            display.style.display='none'
            break;
          
          case 'LOST':
            display.style.display='none'
            break;
            
          case 'TIED':
            display.style.display='none'
            break;
        }
      },
      
      playaudio(){
        document.querySelector('#audio').play()
      },
      
      pauseaudio(){
        document.querySelector('#audio').pause()
      }
        
    } //Methods

}) /*VUE*/








