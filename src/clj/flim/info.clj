(ns flim.store
  (:require [flim.io :as io]
            [clojure.string :as str]
            [taoensso.timbre :as log]
            [com.stuartsierra.component :as component]))

(let [{:keys [status body]} fake]
  (if-not (= status :ok)
    {:status :info-error :body body}
    nil
    )

  )
(def fake {:status :ok,
         :body
         {:page 1,
          :results
          [{:poster_path "/zSuh8dGwqpsWR7ccvYbfxbSZ37o.jpg",
            :video false,
            :popularity 4.76524,
            :release_date "2013-08-16",
            :vote_average 7.7,
            :overview
            "The night after another unsatisfactory New Year party, Tim's father tells his son that the men in his family have always had the ability to travel through time. Tim can't change history, but he can change what happens and has happened in his own life – so he decides to make his world a better place... by getting a girlfriend. Sadly, that turns out not to be as easy as he thinks.",
            :original_language "en",
            :title "About Time",
            :original_title "About Time",
            :vote_count 1405,
            :adult false,
            :backdrop_path "/oNBv90rLY76ifEExrFHdJgLzBQG.jpg",
            :id 122906,
            :genre_ids [35 18 878]}
           {:poster_path "/6R59WCdJvA22VHQNlhsr48gdGxR.jpg",
            :video false,
            :popularity 1.002096,
            :release_date "2013-04-14",
            :vote_average 0,
            :overview
            "A short film about love, loss and letting go. A young man, raised by his father after the death of his mother, finds himself stalled in his life and unable to pursue his dreams of pursuing an acting career. Performing in a play in his hometown, he meets an older actress who acts as a catalyst, changing everything.",
            :original_language "en",
            :title "About Time",
            :original_title "About Time",
            :vote_count 0,
            :adult false,
            :backdrop_path nil,
            :id 356591,
            :genre_ids [18 35]}
           {:poster_path "/jVdh1uwZKRjOZ9AncirPDgCMU3p.jpg",
            :video false,
            :popularity 2.138746,
            :release_date "2009-04-24",
            :vote_average 7.2,
            :overview
            "Three men walk into a bar; two geeks and a cynic. They are three ordinary blokes who all have dreams and hopes for an exciting and better future",
            :original_language "en",
            :title "Frequently Asked Questions About Time Travel",
            :original_title "Frequently Asked Questions About Time Travel",
            :vote_count 91,
            :adult false,
            :backdrop_path "/2qzPibT376A2nLxDGdSmD6J4xek.jpg",
            :id 22494,
            :genre_ids [18 35 878 10749]}
           {:poster_path "/nJiglOtMoWIwBpiGLxMRVqk2GGG.jpg",
            :video false,
            :popularity 1.532057,
            :release_date "1992-07-16",
            :vote_average 4.3,
            :overview
            "When an old clock arrives at home as a gift, strange things begin to happen. The family that proudly assigned a privileged place to the clock in the living room, is unaware that this thing is a link to an old and evil house...",
            :original_language "en",
            :title "Amityville: It's About Time",
            :original_title "Amityville: It's About Time",
            :vote_count 11,
            :adult false,
            :backdrop_path "/zKQ6yUCmcaaHNUfNopSB9R27CTH.jpg",
            :id 41671,
            :genre_ids [27]}
           {:poster_path "/23M0jf4LWK3QPBkDIS5dJlIcLnK.jpg",
            :video false,
            :popularity 1.02201,
            :release_date "2016-05-01",
            :vote_average 8,
            :overview
            "Netflix presents standup and two-time Last Comic Standing finalist Gary Gulman. Titled It’s About Time, the tour marks Gulman’s 20th year in comedy and stops at the Bowery Ballroom in New York.",
            :original_language "en",
            :title "Gary Gulman: It's About Time",
            :original_title "Gary Gulman: It's About Time",
            :vote_count 2,
            :adult false,
            :backdrop_path nil,
            :id 396109,
            :genre_ids [35]}
           {:poster_path "/sSEoz7UcGH4XyeM3uWH3p60WbE3.jpg",
            :video false,
            :popularity 1.000632,
            :release_date "2005-05-24",
            :vote_average 4,
            :overview
            "Crossover comedian Earthquake serves up his straightforward brand of humor in this live stand-up performance. Earthquake's explosive jokes, intelligence and undeniable appeal make him a favorite of both urban and mainstream audiences -- he's appeared on Bill Maher's talk show and gone on the road with the Def Comedy Jam stand-up circuit. This one-hour special showcases the comedian at his unstoppable best.",
            :original_language "en",
            :title "Earthquake: About Got Damm Time",
            :original_title "Earthquake: About Got Damm Time",
            :vote_count 2,
            :adult false,
            :backdrop_path "/4rQDuk6Za344pcbeXfzlC3D0Ubl.jpg",
            :id 216637,
            :genre_ids [35]}
           {:poster_path "/aU1GUdwbNIBAxZhm6rypqLl4I7c.jpg",
            :video false,
            :popularity 1.075023,
            :release_date "2015-06-28",
            :vote_average 0,
            :overview
            "The first major profile of the great British film director Nicolas Roeg, examining his very personal vision of cinema as in such films as Don't Look Now, Performance, Walkabout and The Man Who Fell to Earth. Roeg reflects on his career, which began as a leading cinematographer, and on the themes that have obsessed him, such as our perception of time and the difficulty of human relationships.",
            :original_language "en",
            :title "Nicolas Roeg: It's About Time...",
            :original_title "Nicolas Roeg: It's About Time...",
            :vote_count 0,
            :adult false,
            :backdrop_path nil,
            :id 346879,
            :genre_ids [99]}
           {:poster_path "/6iaaRtPvI7fkFnl0W6f5dlsHpRB.jpg",
            :video false,
            :popularity 1.12403,
            :release_date "1992-09-10",
            :vote_average 0.5,
            :overview
            "Join stars Paula Abdul, Luke Perry, Sinbad, Pauly Shore, Jaleel White and many, many more as they take an entertaining, music-filled and honest look at HIV and AIDS.  You'll get all the latest facts, important dos and don'ts, and you'll meet some wonderful people.  Co-hosts Arsenio Hall and Earvin \"Magic\" Johnson even hit the court for a little one-on-one, and then take \"time out\" for an informative heart-to-heart!  For people who already know about HIV and AIDS, and for those who don't, TIME OUT is a video you can't afford to miss.",
            :original_language "en",
            :title "Time Out: The Truth About HIV, AIDS and You",
            :original_title "Time Out: The Truth About HIV, AIDS and You",
            :vote_count 1,
            :adult false,
            :backdrop_path nil,
            :id 298339,
            :genre_ids [35 99 10402]}
           {:poster_path "/lvAcnQWchqopQXIaVRtJm3rUEXU.jpg",
            :video false,
            :popularity 1.03,
            :release_date "",
            :vote_average 0,
            :overview
            "Crossover comedian Earthquake serves up his straightforward brand of humor in this live stand-up performance . Earthquake's explosive jokes, intelligence and undeniable appeal make him a favorite of both urban and mainstream audiences -- he's appeared on Bill Maher's talk show and gone on the road with the Def Comedy Jam stand-up circuit. This one-hour special showcases the comedian at his unstoppable best.",
            :original_language "en",
            :title
            "Platinum Comedy Series: Earthquake: About Got Damm Time!",
            :original_title
            "Platinum Comedy Series: Earthquake: About Got Damm Time!",
            :vote_count 0,
            :adult false,
            :backdrop_path "/sIyLBMGeCuGTSP7qbc9Rr4TTmA7.jpg",
            :id 313548,
            :genre_ids [35]}
           {:poster_path nil,
            :video false,
            :popularity 1.000739,
            :release_date "2010-02-23",
            :vote_average 0,
            :overview
            "A U.S. Navy Ship vanishes during a secret World War II Experiment gone awry. When it re-appears, observers are horrified to see crew members embedded in the deck and steel of the ship. During a sea trial, the ship vanishes and travels through time setting off a number of events that continue today. You'll meet Al Bielek, Preston Nichols and Duncan Cameron. They are all survivors of U.S. Government Experiments involving INVISIBILITY, TIME TRAVEL, MIND CONTROL, PSYCHIC WARFARE and REMOTE VIEWING.",
            :original_language "en",
            :title
            "The Truth About The Philadelphia Experiment: Invisibility, Time Travel and Mind Control",
            :original_title
            "The Truth About The Philadelphia Experiment: Invisibility, Time Travel and Mind Control",
            :vote_count 0,
            :adult false,
            :backdrop_path nil,
            :id 275574,
            :genre_ids []}
           {:poster_path "/c63SMLYvBh1hzDQSpd81jyU7V4C.jpg",
            :video false,
            :popularity 1.004707,
            :release_date "2014-10-04",
            :vote_average 0,
            :overview
            "From small town neighborhoods to cities, the shift in the economy and continued financial struggles is having an adverse effect on communities and creating devastating isolation for its inhabitants. As a result, time banks begin to form encouraging members to repair and rebuild their community without cash. But, new time bankers must adapt to a new system without money in order to keep their neighborhoods strong and flourishing.",
            :original_language "en",
            :title "Time As Money",
            :original_title "Time As Money",
            :vote_count 0,
            :adult false,
            :backdrop_path nil,
            :id 296381,
            :genre_ids [99]}
           {:poster_path nil,
            :video false,
            :popularity 1.000078,
            :release_date "",
            :vote_average 0,
            :overview
            "9  Mexican directors come together to narrate traditions and more brutal, ruthless and bizarre legends of our country.  Mexico Barbaro shows the world stories that are part of our popular culture, from sweet stories told by our grandmothers, the tooth fairy, witchcraft, the story behind the weeping woman, sexy Devil' servers, a pagan hero, the burnt woman, up to ancestral culinary bloody rites.  Traditions and legends that today continue to cause terror among Mexican people.  Included shorts: \"Paidós Phobos\" (Paidos Phobos)  \"Potzonalli\" (Potzonalli)  \"Bolas De Fuego\" (Fireballs)  \"Exodoncia\" (Exodontia)  \"Vitriol\" (Vitriol) Dirección  \"La Leyenda de Juan Soldado\" (Juan the Soldier)  \"No Te Duermas\" (Do not sleep)  \"Ya Es Hora\" (It's About time)",
            :original_language "es",
            :title "México Bárbaro II",
            :original_title "México Bárbaro II",
            :vote_count 0,
            :adult false,
            :backdrop_path "/b20wN3OqC6MxZYB6d7WQDGHuNsW.jpg",
            :id 430223,
            :genre_ids []}],
          :total_results 12,
          :total_pages 1}})
