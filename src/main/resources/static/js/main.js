const watchedCount = document.getElementById('watchedCount')
const movies = Array.from(document.getElementsByClassName('movie'))

function getWatchedCount() {
  return document.getElementsByClassName('watched').length
}

function updateMoviesFromFiltersAndSearchAndHidden() {
  const activeFilters = Object.keys(filters)
    .filter(filter => filters[filter])

  movies.forEach(movie => {
    movie.classList.remove('hide')
  })

  const predicates = []

  if (hideWatched) {
    predicates.push(movie => movie.classList.contains('watched'))
  }

  if (searchTerm.length > 0) {
    predicates.push(movie => !(new RegExp(searchTerm, 'gi').test(movie.id)))
  }

  if (activeFilters.length > 0) {
    predicates.push(movie => !activeFilters.every(filter =>
        movie.children[1].children[1].children[0].children[3].children[4].textContent.includes(filter)))
  }

  if (predicates.length > 0) {
      movies
        .filter(movie => predicates.some(predicate => predicate(movie)))
        .forEach(movie => movie.classList.add('hide'))
  }
}

movies.forEach(el => {
  el.addEventListener('click', event => {
    const movie = el.id

    console.log(`Fetching movie with: ${movie}`)

    fetch(`/movies/${movie}`, { method: 'POST'})
      .then(res => res.json())
      .then(res => {
        console.log(`Res: ${res}`)
        if (res) {
          el.classList.toggle('watched')
          watchedCount.textContent = getWatchedCount()
          updateMoviesFromFiltersAndSearchAndHidden()
        }
      })
      .catch(err => console.error(err))
  })
})

const filters = {}

Array.from(document.getElementsByClassName('filter')).forEach(el => {
  const filter = el.textContent
  filters[filter] = false

  el.addEventListener('click', event => {
    el.classList.toggle('active')
    filters[filter] = !filters[filter]
    updateMoviesFromFiltersAndSearchAndHidden()
  })
})

let searchTerm = ''

document.getElementById('search').addEventListener('keyup', event => {
    searchTerm = event.target.value;
    updateMoviesFromFiltersAndSearchAndHidden()
})

let hideWatched = false

document.getElementById('hide-watched').addEventListener('click', event => {
    hideWatched = !hideWatched
    event.target.classList.toggle('active')
    updateMoviesFromFiltersAndSearchAndHidden()
})