<h1>Reversi IA - Projet d'Intelligence Artificielle</h1>
<p><strong>Un jeu de Reversi (Othello) avec plusieurs algorithmes d'IA, interface graphique complète, et système de tests comparatifs</strong></p>

<h2>Introduction</h2>
<p>Ce projet a été réalisé dans le cadre du cours d'Intelligence Artificielle du L3 MIASHS 2025-2026. Il implémente le jeu de stratégie Reversi (Othello) avec une interface graphique Java Swing et plusieurs algorithmes d'IA avancés pour permettre des parties entre humains, entre bots, ou humain vs IA.</p>

<p>Le projet inclut une interface complète pour jouer au Reversi, un système de tests automatisés pour comparer les performances des différents algorithmes, et une architecture facilitant l'ajout de nouvelles stratégies d'IA. La fonctionnalité de tests comparatifs permet d'exécuter des centaines ou milliers de parties entre différents bots pour analyser statistiquement leurs performances relatives.</p>

<h2>Lien pour Javadoc</h2>
<p><a href="https://noahdehaene-coder.github.io/Projet-IA-2025-reversi/reversi/package-summary.html">https://noahdehaene-coder.github.io/Projet-IA-2025-reversi/reversi/package-summary.html</a></p>

<h2>Instructions d'installation et d'exécution</h2>
<ol>
<li>Ouvrez Eclipse.</li>
<li>Sélectionnez <code>Window > Show View > Other</code>.</li>
<li>Parcourez les options et sélectionnez <code>Git > Git Repositories</code>.</li>
<li>Dans la vue Git Repositories, cliquez sur <code>Clone a Git Repository</code>.</li>
<li>Dans Clone URL, entrez dans la boîte URL : 
<pre style="background-color: #f6f8fa; border: 1px solid #e1e4e8; border-radius: 6px; padding: 16px; margin: 10px 0; overflow: auto;">
<code>git clone https://github.com/[votre-username]/reversi-ai-project.git</code>
</pre></li>
<li>Appuyez sur <code>Next</code> jusqu'à arriver à <code>Local Destination</code>, où vous pouvez choisir le dossier dans lequel le projet sera stocké.</li>
<li>Appuyez sur <code>Finish</code>.</li>
<li>Le projet apparaîtra dans les Git repositories. Faites un clic droit dessus, sélectionnez <code>Import Projects</code>, puis <code>Finish</code>.</li>
</ol>

<h2>Utilisation</h2>
<h3>Lancer une partie normale :</h3>
<ul>
<li>Exécutez <code>Main.java</code></li>
<li>Une fenêtre de sélection des joueurs s'ouvre</li>
<li>Choisissez "Humain", "Bot Aléatoire" ou un autre algorithme d'IA pour chaque joueur</li>
<li>Cliquez sur "Commencer la Partie"</li>
<li><strong>À la fin d'une partie :</strong> Le bouton <em>Rejouer</em> répète la partie avec les mêmes choix de joueurs, tandis que le bouton <em>Nouvelle Partie</em> nous ramène à la fenêtre de sélection des joueurs initiale</li>
</ul>

<h3>Lancer des tests entre bots :</h3>
<ul>
<li>Dans la fenêtre de sélection des joueurs, cliquez sur "Simuler plusieurs parties"</li>
<li>Configurez les types de bots, le nombre de parties (1 à 10000)</li>
<li>Cliquez sur "Exécuter les Tests" pour lancer la simulation</li>
<li>Les résultats statistiques s'affichent à la fin</li>
</ul>

<h2>Algorithme d'IA implémentés</h2>
<ul>
<li><strong>Bot Aléatoire</strong> : Choisit un coup valide au hasard</li>
<li><strong>BFS Bot</strong> : Recherche en largeur d'abord</li>
<li><strong>DFS Bot</strong> : Recherche en profondeur d'abord</li>
<li><strong>Dijkstra Bot</strong> : Algorithme de Dijkstra adapté au jeu de reversi</li>
<li><strong>Greedy BFS Bot</strong> : Recherche en largeur gourmande sans recherche en profondeur</li>
<li><strong>A* Bot</strong> : Algorithme A* avec heuristique</li>
<li><strong>AlphaBeta</strong> : Algorithme Minimax avec élagage alpha-beta</li>
<li><strong>Monte Carlo</strong> : Simulation Monte Carlo</li>
<li><strong>AlphaBeta Rapide</strong> : Version optimisée avec représentation bit à bit</li>
<li><strong>Dijkstra Rapide</strong> : Version optimisée de Dijkstra avec représentation bit à bit</li>
</ul>

<h2>Projet par:</h2>
<p>Joe HAJJ ASSAF (BigSealFan)</p>
<p>Noah DEHAENE (noahdehaene-coder)</p>
<p>Amine EL FEJER (Thatonethereyes)</p>