import React, {useState} from 'react'
import useSWR from 'swr'
import xor from 'lodash.xor'

import Nav from './Nav'
import Controls from './Controls'
import Movie from './Movie'
import Loader from './Loader'

import {isMovieWatched} from './utils'

function createGenresArray(movies) {
  return movies
    ?.flatMap(movie => movie.genre.split(', '))
    .filter((genre, index, arr) => arr.indexOf(genre) === index)
    .sort()
}

function App() {
  const {data: movies} = useSWR('/api/v1/movies');
  const {data: watchList} = useSWR("/api/v1/movies/watchList")

  const [genreFilter, setGenreFilter] = useState([])
  const [hideWatched, setHideWatched] = useState(false)
  const [searchTerm, setSearchTerm] = useState('')

  const genres = createGenresArray(movies)

  function updateGenreFilter(genre) {
    setGenreFilter(xor(genreFilter, [genre]))
  }

  return (
    <>
      <Nav/>
      <main className="container">
        <Controls
          genres={genres}
          genreFilter={genreFilter}
          updateGenreFilter={updateGenreFilter}
          setHideWatched={setHideWatched}
          hideWatched={hideWatched}
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}/>
        <div className="row">
          {!movies && <Loader classes="col-sm-1 offset-sm-5"/>}
          {movies
            ?.filter(movie => genreFilter.length === 0 || genreFilter.every(genre => movie.genre.includes(genre)))
            .filter(movie => !(hideWatched && isMovieWatched(watchList, movie.title)))
            .filter(movie => new RegExp(searchTerm, 'ig').test(movie.title))
            .map(movie => <Movie key={movie.title} movie={movie}/>)}
        </div>
      </main>
    </>
  )
}

export default App
