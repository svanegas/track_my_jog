require 'rails_helper'

RSpec.describe "Auth::Sessions", type: :request do

  describe "POST /auths/sign_in" do
    let(:password) { Faker::Internet.password(8) }
    let(:existing_user) { create(:user, password: password) }
    let(:body) { { email: existing_user.email, password: password } }

    context 'when email and password are valid' do
      it 'responds with 200' do
        post user_session_path, { params: body }
        expect(response).to have_http_status(200)
      end

      it 'responds with access token in headers' do
        post user_session_path, { params: body }
        expect(response.headers['access-token']).to be_present
      end

      it 'responds with token type in headers' do
        post user_session_path, { params: body }
        expect(response.headers['token-type']).to be_present
      end

      it 'responds with client in headers' do
        post user_session_path, { params: body }
        expect(response.headers['client']).to be_present
      end

      it 'responds with uid in headers' do
        post user_session_path, { params: body }
        expect(response.headers['uid']).to be_present
      end

      it 'responds with user in body' do
        post user_session_path, { params: body }
        user = JSON.parse(response.body)
        expect(user['id']).to be_present
        expect(user['name']).to eq existing_user.name
        expect(user['email']).to eq existing_user.email
      end
    end

    context 'when email does not exist' do
      it 'responds with 401' do
        post user_session_path, { params: body.merge(email: 'non-existing-email@email.co') }
        expect(response).to have_http_status(401)
      end
    end

    context 'when password is incorrect' do
      let(:wrong_pass_body) do
        { email: existing_user.email, password: password[0..-2] } # Ignore last char from password.
      end
      it 'responds with 401' do
        post user_session_path, { params: wrong_pass_body }
        expect(response).to have_http_status(401)
      end
    end
  end
end
