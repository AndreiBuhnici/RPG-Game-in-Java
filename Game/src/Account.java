import java.util.List;

public class Account {
    Information info;
    List<Character> chars;
    int maps_completed;

    public class Information {
        private Credentials credentials;
        private List<String> fav_games;
        private String name;
        private String country;


        private Information(){
        }

        public static class InformationBuilder {
            private final Credentials credentials;
            private List<String> fav_games;
            private final String name;
            private String country;

            public InformationBuilder(Credentials credentials, String name) throws InformationIncompleteException {
                if(credentials == null || name == null)
                    throw new InformationIncompleteException("Credentials or name are null.");
                this.credentials = credentials;
                this.name = name;
            }

            public InformationBuilder withFavoriteGames(List<String> fav_games){
                this.fav_games = fav_games;
                return this;
            }

            public InformationBuilder fromCountry(String country){
                this.country = country;
                return this;
            }

            public Information build(){
                Information information = new Account().new Information();
                information.credentials = this.credentials;
                information.fav_games = this.fav_games;
                information.name = this.name;
                information.country = this.country;

                return information;
            }

        }

        public Credentials getCredentials() {
            return credentials;
        }

        public List<String> getFav_games() {
            return fav_games;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public void setFav_games(List<String> fav_games) {
            this.fav_games = fav_games;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public Account(){}

    public Account(Credentials credentials, List<String> fav_games, String name, String country,
                   List<Character> chars, int games_played) throws InformationIncompleteException {
        info = new Information.InformationBuilder(credentials,name)
                .withFavoriteGames(fav_games)
                .fromCountry(country)
                .build();
        this.chars = chars;
        this.maps_completed = games_played;
    }
}
