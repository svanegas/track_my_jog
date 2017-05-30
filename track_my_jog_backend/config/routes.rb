Rails.application.routes.draw do
  mount_devise_token_auth_for 'User', at: 'auths', skip: [:omniauth_callbacks], controllers: {
    sessions:  'overrides/sessions',
    registrations: 'overrides/registrations'
  }
  get '/time_entries/report/:date', to: 'time_entries#report'
  resources :time_entries
  resources :users
end
