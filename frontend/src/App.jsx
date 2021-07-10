import React, {useState} from 'react'
import useSWR from 'swr'
import xor from 'lodash.xor'

import Nav from './Nav'
import Controls from './Controls'
import Movie from './Movie'

function createGenresArray(movies) {
  return movies
    ?.flatMap(movie => movie.genre.split(', '))
    .filter((genre, index, arr) => arr.indexOf(genre) === index)
    .sort()
}

function App() {
  const {data: movies} = useSWR('/api/v1/movies');
  const [genreFilter, setGenreFilter] = useState([])

  const genres = createGenresArray(movies)

  function updateGenreFilter(genre) {
    setGenreFilter(xor(genreFilter, [genre]))
  }

  return (
    <>
      <Nav/>
      <main className="container">
        <Controls genres={genres} genreFilter={genreFilter} updateGenreFilter={updateGenreFilter}/>
        <div className="row">
          {!movies && <div className="col-12">Loading Movies</div>}
          {movies
            ?.filter(movie => genreFilter.length === 0 || genreFilter.every(genre => movie.genre.includes(genre)))
            .map(movie => <Movie key={movie.title} movie={movie}/>)}
        </div>
      </main>
    </>
  )
}

export default App
