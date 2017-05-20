class TimeEntriesController < ApplicationController
  before_action :set_time_entry, only: [:show, :update, :destroy]
  before_action :authenticate_user!

  # GET /time_entries
  def index
    if current_user.admin?
      @time_entries = TimeEntry.all
    else
      @time_entries = current_user.time_entries
    end

    begin
      filtering_params(params).each do |key, value|
        @time_entries = @time_entries.public_send(key, value) if value.present?
      end
      render json: @time_entries
    rescue ArgumentError
      render json: {
        errors: [I18n.t('time_entry.date_query.invalid_format')]
      }, status: :bad_request
    end
  end

  # GET /time_entries/1
  def show
    render json: @time_entry
  end

  # POST /time_entries
  def create
    @time_entry = TimeEntry.new(time_entry_params.merge(user: current_user))
    @time_entry.user_id = params[:user_id] if current_user.admin? && params[:user_id].present?

    if @time_entry.save
      render json: @time_entry, status: :ok
    else
      render json: {
        errors: @time_entry.errors.full_messages
      }, status: :unprocessable_entity
    end
  end

  # PATCH/PUT /time_entries/1
  def update
    if @time_entry.update(time_entry_params)
      render json: @time_entry
    else
      render json: @time_entry.errors, status: :unprocessable_entity
    end
  end

  # DELETE /time_entries/1
  def destroy
    @time_entry.destroy
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_time_entry
      @time_entry = TimeEntry.find(params[:id])
      unless current_user.admin? || @time_entry.user == current_user
        render json: {
          errors: [I18n.t('time_entry.forbidden_entry')],
        }, status: :forbidden
      end
    end

    # Only allow a trusted parameter "white list" through.
    def time_entry_params
      params.permit(:date, :distance, :duration)
    end

    # Slice to query params that are allowed in index method.
    def filtering_params(params)
      filter_params = params.slice(:user_id, :date_from, :date_to)
      filter_params.delete(:user_id) unless current_user.admin?
      filter_params
    end
end