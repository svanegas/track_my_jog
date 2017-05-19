require 'rails_helper'

RSpec.describe "TimeEntries", type: :request do

  describe "GET /time_entries" do
    context 'as regular user' do
      before do
        @user = create(:user)
        @session = @user.create_new_auth_token
      end

      context 'when he owns no time entries' do
        it 'responds with an empty array' do
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          expect(entries).to be_empty
        end
      end

      context 'when he owns time entries' do
        before do
          Faker::Number.between(1, 10).times { create(:time_entry, user: @user) }
        end

        it 'responds with an array containing each time entry' do
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          entries.each do |entry|
            expect(entry['user_id']).to eq @user.id
          end
        end

        it 'does not return entries from other user' do
          @other_user = create(:user)
          Faker::Number.between(1, 10).times { create(:time_entry, user: @other_user) }
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          entries.each do |entry|
            expect(entry['user_id']).not_to eq @other_user.id
          end
        end
      end
    end

    context 'as manager user' do
      before do
        @user = create(:manager_user)
        @session = @user.create_new_auth_token
      end

      context 'when he owns no time entries' do
        it 'responds with an empty array' do
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          expect(entries).to be_empty
        end
      end

      context 'when he owns time entries' do
        before do
          Faker::Number.between(1, 10).times { create(:time_entry, user: @user) }
        end

        it 'responds with an array containing each time entry' do
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          entries.each do |entry|
            expect(entry['user_id']).to eq @user.id
          end
        end

        it 'does not return entries from other user' do
          @other_user = create(:user)
          Faker::Number.between(1, 10).times { create(:time_entry, user: @other_user) }
          get time_entries_path, { headers: @session }
          entries = JSON.parse(response.body)
          entries.each do |entry|
            expect(entry['user_id']).not_to eq @other_user.id
          end
        end
      end
    end

    context 'as admin user' do
      before do
        @user = create(:admin_user)
        @session = @user.create_new_auth_token
      end

      context 'when no query params' do
        context 'when no one owns time entries' do
          it 'responds with an empty array' do
            get time_entries_path, { headers: @session }
            entries = JSON.parse(response.body)
            expect(entries).to be_empty
          end
        end

        context 'when only he owns time entries' do
          before do
            Faker::Number.between(1, 10).times { create(:time_entry, user: @user) }
          end

          it 'responds with an array containing each time entry' do
            get time_entries_path, { headers: @session }
            entries = JSON.parse(response.body)
            entries.each { |entry| expect(entry['user_id']).to eq @user.id }
          end
        end

        context 'when several people own time entries' do
          it 'responds with entries from all user' do
            @other_user = create(:user)
            Faker::Number.between(1, 10).times { create(:time_entry, user: @other_user) }
            get time_entries_path, { headers: @session }
            entries = JSON.parse(response.body)
            user_ids = Set.new
            entries.each { |entry| user_ids.add(entry['user_id']) }
            expect(user_ids.subset?([@user.id, @other_user.id].to_set)).to be true
          end
        end
      end

      context "when 'user_id' query param is included" do
        it 'only returns time entries that belong to the given user id' do
          Faker::Number.between(1, 10).times { create(:time_entry) } # Create some other entries
          @other_user = create(:user)
          Faker::Number.between(1, 10).times { create(:time_entry, user: @other_user) }
          get time_entries_path, { params: { user_id: @other_user.id }, headers: @session }
          entries = JSON.parse(response.body)
          entries.each { |entry| expect(entry['user_id']).to eq @other_user.id }
        end
      end

      context "when 'date_from' query param is included" do
        context 'when not proper formatted' do
          it 'responds with 400' do
            get time_entries_path, { params: { date_from: 'not-a-date' }, headers: @session }
            expect(response.status).to eq(400)
          end
        end

        context 'when proper formatted' do
          it 'responds with entries having date greater or equal to given' do
            after_10_days = []
            Faker::Number.between(1, 10).times do # Create some old entries
              create(:time_entry, date: Faker::Date.between(100.days.ago, 11.days.ago))
            end
            Faker::Number.between(1, 10).times do # Create some recent entries
              after_10_days << create(:time_entry, {
                date: Faker::Date.between(10.days.ago, Date.today)
              }).id
            end
            get time_entries_path, { params: { date_from: 10.days.ago }, headers: @session }
            entries = JSON.parse(response.body)
            expect(entries.count).to eq after_10_days.count
            entries.each { |entry| expect(after_10_days).to include(entry['id']) }
          end
        end
      end

      context "when 'date_to' query param is included" do
        context 'when not proper formatted' do
          it 'responds with 400' do
            get time_entries_path, { params: { date_to: 'not-a-date' }, headers: @session }
            expect(response.status).to eq(400)
          end
        end

        context 'when proper formatted' do
          it 'responds with entries having date less or equal to given' do
            before_10_days = []
            Faker::Number.between(1, 10).times do # Create some old entries
              before_10_days << create(:time_entry, {
                date: Faker::Date.between(100.days.ago, 10.days.ago)
              }).id
            end
            Faker::Number.between(1, 10).times do # Create some recent entries
              create(:time_entry, date: Faker::Date.between(9.days.ago, Date.today))
            end
            get time_entries_path, { params: { date_to: 10.days.ago }, headers: @session }
            entries = JSON.parse(response.body)
            expect(entries.count).to eq before_10_days.count
            entries.each { |entry| expect(before_10_days).to include(entry['id']) }
          end
        end
      end
    end
  end

  describe "GET /time_entries/:id" do
    context 'when requested time entry does not exist' do
      it 'raises ActiveRecord::RecordNotFound exception' do
        expect do
          get time_entry_path(Faker::Number.between(9999, 99999)), {
            headers: create(:user).create_new_auth_token
          }
        end.to raise_error ActiveRecord::RecordNotFound
      end
    end

    context 'as regular user' do
      before do
        @user = create(:user)
        @session = @user.create_new_auth_token
      end

      context 'when requested time entry belongs to him' do
        before do
          @time_entry = create(:time_entry, user: @user)
        end

        it 'responds with 200' do
          get time_entry_path(@time_entry.id), { headers: @session }
          expect(response.status).to eq(200)
        end

        it 'responds with the requested entry information' do
          get time_entry_path(@time_entry.id), { headers: @session }
          time_entry = JSON.parse(response.body)
          expect(time_entry['id']).to eq(@time_entry.id)
          expect(time_entry['distance']).to eq(@time_entry.distance)
          expect(time_entry['duration']).to eq(@time_entry.duration)
        end
      end

      context 'when requested time entry does not belong to him' do
        it 'responds with 403' do
          time_entry = create(:time_entry)
          get time_entry_path(time_entry.id), { headers: @session }
          expect(response.status).to eq(403)
        end
      end
    end

    context 'as manager user' do
      before do
        @user = create(:manager_user)
        @session = @user.create_new_auth_token
      end

      context 'when requested time entry belongs to him' do
        before do
          @time_entry = create(:time_entry, user: @user)
        end

        it 'responds with 200' do
          get time_entry_path(@time_entry.id), { headers: @session }
          expect(response.status).to eq(200)
        end

        it 'responds with the requested entry information' do
          get time_entry_path(@time_entry.id), { headers: @session }
          time_entry = JSON.parse(response.body)
          expect(time_entry['id']).to eq(@time_entry.id)
          expect(time_entry['distance']).to eq(@time_entry.distance)
          expect(time_entry['duration']).to eq(@time_entry.duration)
        end
      end

      context 'when requested time entry does not belong to him' do
        it 'responds with 403' do
          time_entry = create(:time_entry)
          get time_entry_path(time_entry.id), { headers: @session }
          expect(response.status).to eq(403)
        end
      end
    end

    context 'as admin user' do
      before do
        @user = create(:admin_user)
        @session = @user.create_new_auth_token
      end

      context 'when requested time entry belongs to him' do
        before do
          @time_entry = create(:time_entry, user: @user)
        end

        it 'responds with 200' do
          get time_entry_path(@time_entry.id), { headers: @session }
          expect(response.status).to eq(200)
        end

        it 'responds with the requested entry information' do
          get time_entry_path(@time_entry.id), { headers: @session }
          time_entry = JSON.parse(response.body)
          expect(time_entry['id']).to eq(@time_entry.id)
          expect(time_entry['distance']).to eq(@time_entry.distance)
          expect(time_entry['duration']).to eq(@time_entry.duration)
        end
      end

      context 'when requested time entry does not belong to him' do
        before do
          @time_entry = create(:time_entry)
        end

        it 'responds with 200' do
          get time_entry_path(@time_entry.id), { headers: @session }
          expect(response.status).to eq(200)
        end

        it 'responds with the requested entry information' do
          get time_entry_path(@time_entry.id), { headers: @session }
          time_entry = JSON.parse(response.body)
          expect(time_entry['id']).to eq(@time_entry.id)
          expect(time_entry['distance']).to eq(@time_entry.distance)
          expect(time_entry['duration']).to eq(@time_entry.duration)
          expect(time_entry['user_id']).not_to eq(@user.id)
        end
      end
    end
  end

  describe "POST /time_entries" do
    let(:user) { create(:user) }
    let(:session) { user.create_new_auth_token }
    let(:body) { attributes_for(:time_entry) }

    context 'when all attributes are valid' do
      it 'responds with 200' do
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(200)
      end

      it 'responds with created time entry information' do
        post time_entries_path, { params: body, headers: session }
        time_entry = JSON.parse(response.body)
        expect(time_entry['user_id']).to eq(user.id)
        expect(time_entry['duration']).to eq(body[:duration])
        expect(time_entry['distance']).to eq(body[:distance])
      end
    end

    context 'when duration is nil' do
      it 'responds with 422' do
        body[:duration] = nil
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when duration is empty' do
      it 'responds with 422' do
        body[:duration] = ''
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when duration is negative' do
      it 'responds with 422' do
        body[:duration] = Faker::Number.negative.to_i
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when duration is zero' do
      it 'responds with 422' do
        body[:duration] = 0
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when distance is nil' do
      it 'responds with 422' do
        body[:distance] = nil
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when distance is empty' do
      it 'responds with 422' do
        body[:distance] = ''
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when distance is negative' do
      it 'responds with 422' do
        body[:distance] = Faker::Number.negative.to_i
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when distance is zero' do
      it 'responds with 422' do
        body[:distance] = 0
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when date is nil' do
      it 'responds with 422' do
        body[:date] = nil
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when date is empty' do
      it 'responds with 422' do
        body[:date] = ''
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'when date has invalid format' do
      it 'responds with 400' do
        body[:date] = Faker::Lorem.characters
        post time_entries_path, { params: body, headers: session }
        expect(response.status).to eq(422)
      end
    end

    context 'as admin user' do
      let(:admin_user) { create(:admin_user) }
      let(:admin_session) { admin_user.create_new_auth_token }

      context "when 'user_id' param is not included" do
        it 'responds with 200' do
          post time_entries_path, { params: body, headers: admin_session }
          expect(response.status).to eq(200)
        end

        it 'responds with time entry belonging to the admin' do
          post time_entries_path, { params: body, headers: admin_session }
          time_entry = JSON.parse(response.body)
          expect(time_entry['user_id']).to eq(admin_user.id)
        end
      end

      context "when 'user_id' param is included" do
        context 'when given user exists' do
          it 'responds with 200' do
            body[:user_id] = user.id
            post time_entries_path, { params: body, headers: admin_session }
            expect(response.status).to eq(200)
          end

          it 'responds with time entry assigned to requested user' do
            body[:user_id] = user.id
            post time_entries_path, { params: body, headers: admin_session }
            time_entry = JSON.parse(response.body)
            expect(time_entry['user_id']).to eq(user.id)
            expect(time_entry['user_id']).not_to eq(admin_user.id)
          end
        end

        context 'when given user does not exist' do
          it 'responds with 422' do
            body[:user_id] = Faker::Number.between(9999, 99999)
            post time_entries_path, { params: body, headers: admin_session }
            expect(response.status).to eq(422)
          end
        end
      end
    end
  end
end
