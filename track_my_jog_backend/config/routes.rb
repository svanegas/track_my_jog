Rails.application.routes.draw do
  mount_devise_token_auth_for 'User', at: 'auths', skip: [:omniauth_callbacks], controllers: {
    sessions:  'overrides/sessions',
    registrations: 'overrides/registrations'
  }
  resources :time_entries
end
